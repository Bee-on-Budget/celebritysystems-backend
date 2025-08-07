package com.celebritysystems.scheduler;

import com.celebritysystems.entity.Contract;
import com.celebritysystems.entity.User;
import com.celebritysystems.repository.ContractRepository;
import com.celebritysystems.repository.UserRepository;
import com.celebritysystems.service.OneSignalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractExpiryNotificationScheduler {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final OneSignalService oneSignalService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Scheduled task that runs daily at 9:00 AM to check for contracts expiring within 7 days
     * and send notifications to all users in the affected companies
     */
    @Scheduled(cron = "0 0 9 * * *") // Runs every day at 9:00 AM
    public void checkAndNotifyExpiringContracts() {
        log.info("Starting daily contract expiry notification check...");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sevenDaysFromNow = now.plusDays(7);
            
            List<Contract> expiringContracts = contractRepository.findByExpiredAtBetween(now, sevenDaysFromNow);
            
            if (expiringContracts.isEmpty()) {
                log.info("No contracts expiring in the next 7 days");
                return;
            }
            
            log.info("Found {} contracts expiring within 7 days", expiringContracts.size());
            
            Map<Long, List<Contract>> contractsByCompany = expiringContracts.stream()
                    .filter(contract -> contract.getCompanyId() != null)
                    .collect(Collectors.groupingBy(Contract::getCompanyId));
            
            for (Map.Entry<Long, List<Contract>> entry : contractsByCompany.entrySet()) {
                Long companyId = entry.getKey();
                List<Contract> companyContracts = entry.getValue();
                
                sendExpiryNotificationToCompany(companyId, companyContracts);
            }
            
            log.info("Contract expiry notification check completed successfully");
            
        } catch (Exception e) {
            log.error("Error occurred during contract expiry notification check", e);
        }
    }

    /**
     * Send contract expiry notifications to all users in a specific company
     */
    private void sendExpiryNotificationToCompany(Long companyId, List<Contract> expiringContracts) {
        try {
            List<User> companyUsers = userRepository.findByCompanyIdAndPlayerIdIsNotNull(companyId);
            
            if (companyUsers.isEmpty()) {
                log.warn("No users with playerIds found for company ID: {}", companyId);
                return;
            }

            // Extract player IDs
            List<String> playerIds = companyUsers.stream()
                    .map(User::getPlayerId)
                    .filter(playerId -> playerId != null && !playerId.trim().isEmpty())
                    .collect(Collectors.toList());

            if (playerIds.isEmpty()) {
                log.warn("No valid playerIds found for company ID: {}", companyId);
                return;
            }

            // Get company name from the first user (since all users belong to the same company)
            String companyName = companyUsers.get(0).getCompany() != null 
                ? companyUsers.get(0).getCompany().getName() 
                : "Your Company";

            // Prepare notification content
            String title = createNotificationTitle(expiringContracts.size());
            String message = createNotificationMessage(companyName, expiringContracts);
            
            // Prepare additional data
            Map<String, Object> data = createNotificationData(companyId, companyName, expiringContracts);
            
            // Send notification
            oneSignalService.sendWithData(title, message, data, playerIds);
            
            log.info("Contract expiry notification sent to {} users in company '{}' for {} expiring contracts", 
                playerIds.size(), companyName, expiringContracts.size());
                
        } catch (Exception e) {
            log.error("Failed to send contract expiry notification for company ID: {}", companyId, e);
        }
    }

    /**
     * Create notification title based on number of expiring contracts
     */
    private String createNotificationTitle(int contractCount) {
        if (contractCount == 1) {
            return "Contract Expiring Soon";
        } else {
            return "Contracts Expiring Soon";
        }
    }

    /**
     * Create notification message with contract details
     */
    private String createNotificationMessage(String companyName, List<Contract> contracts) {
        if (contracts.size() == 1) {
            Contract contract = contracts.get(0);
            long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDateTime.now(), contract.getExpiredAt());
            
            return String.format("Your contract '%s' will expire in %d day%s (%s)", 
                contract.getInfo() != null ? contract.getInfo() : "Contract",
                daysUntilExpiry,
                daysUntilExpiry == 1 ? "" : "s",
                contract.getExpiredAt().format(DATE_FORMATTER));
        } else {
            return String.format("You have %d contracts expiring within the next 7 days. Please review them.", 
                contracts.size());
        }
    }

    /**
     * Create notification data with contract information
     */
    private Map<String, Object> createNotificationData(Long companyId, String companyName, List<Contract> contracts) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("notificationType", "CONTRACT_EXPIRY_WARNING");
        data.put("companyId", companyId.toString());
        data.put("companyName", companyName);
        data.put("contractCount", contracts.size());
        data.put("checkDate", LocalDateTime.now().format(DATE_FORMATTER));
        
        // Add contract details
        List<Map<String, Object>> contractDetails = contracts.stream()
                .map(this::createContractDetailMap)
                .collect(Collectors.toList());
        
        data.put("contracts", contractDetails);
        
        return data;
    }

    /**
     * Create a map with contract details for notification data
     */
    private Map<String, Object> createContractDetailMap(Contract contract) {
        Map<String, Object> contractMap = new HashMap<>();
        
        contractMap.put("id", contract.getId().toString());
        contractMap.put("info", contract.getInfo() != null ? contract.getInfo() : "");
        contractMap.put("accountName", contract.getAccountName() != null ? contract.getAccountName() : "");
        contractMap.put("expiryDate", contract.getExpiredAt().format(DATE_FORMATTER));
        contractMap.put("contractValue", contract.getContractValue() != null ? contract.getContractValue().toString() : "0");
        
        // Calculate days until expiry
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDateTime.now(), contract.getExpiredAt());
        contractMap.put("daysUntilExpiry", daysUntilExpiry);
        
        // Add contract type information if available
        if (contract.getDurationType() != null) {
            contractMap.put("durationType", contract.getDurationType().name());
        }
        if (contract.getSupplyType() != null) {
            contractMap.put("supplyType", contract.getSupplyType().name());
        }
        if (contract.getOperatorType() != null) {
            contractMap.put("operatorType", contract.getOperatorType().name());
        }
        
        return contractMap;
    }

    /**
     * Manual trigger for testing purposes - can be called via endpoint
     */
    public void triggerManualCheck() {
        log.info("Manual contract expiry check triggered");
        checkAndNotifyExpiringContracts();
    }
}