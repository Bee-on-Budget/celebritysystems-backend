package com.celebritysystems.service.impl;

import com.celebritysystems.dto.AccountPermissionDTO;
import com.celebritysystems.dto.ContractResponseDTO;
import com.celebritysystems.dto.CreateContractDTO;
import com.celebritysystems.dto.ScreenResponse;
import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
import com.celebritysystems.entity.AccountPermission;
import com.celebritysystems.entity.Contract;
import com.celebritysystems.entity.enums.ContractType;
import com.celebritysystems.entity.enums.OperatorType;
import com.celebritysystems.entity.enums.SupplyType;
import com.celebritysystems.repository.CompanyRepository;
import com.celebritysystems.repository.ContractRepository;
import com.celebritysystems.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final CompanyRepository companyRepository;
    private final ScreenServiceImpl screenService;

    @Override
    public Contract createContract(Contract contract) {
        log.info("Creating new contract for company: {}", contract.getCompanyId());
        
        if (contract.getScreenIds() != null) {
            List<Long> validScreenIds = contract.getScreenIds()
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (validScreenIds.isEmpty()) {
                throw new IllegalArgumentException("Screen IDs must not be null or empty.");
            }

            // Check for existing active contracts for each screen
            for (Long screenId : validScreenIds) {
                List<Contract> existingContracts = getContractsByScreen(screenId);
                for (Contract existingContract : existingContracts) {
                    if (existingContract.getExpiredAt().isAfter(LocalDateTime.now())) {
                        throw new IllegalStateException("Screen with id " + screenId + " already has an ongoing contract.");
                    }
                }

                // Check if contract already exists for this company and screen
                if (contract.getCompanyId() != null &&
                        contractExistsForCompanyAndScreen(contract.getCompanyId(), screenId)) {
                    throw new IllegalArgumentException("Active contract already exists for company " + 
                            contract.getCompanyId() + " and screen " + screenId);
                }
            }

            contract.setScreenIds(validScreenIds);
        }

        if (contract.getStartContractAt() == null) {
            contract.setStartContractAt(LocalDateTime.now());
        }

        Contract savedContract = contractRepository.save(contract);
        log.info("Contract created successfully with ID: {}", savedContract.getId());
        return savedContract;
    }

    @Override
    public Contract createContractFromDTO(CreateContractDTO dto) {
        log.info("Creating contract from DTO for company: {}", dto.getCompanyId());
        
        List<AccountPermission> permissions = dto.getAccountPermissions() != null
                ? dto.getAccountPermissions().stream()
                    .map(this::mapToEntity)
                    .distinct() // Ensures only unique entries
                    .collect(Collectors.toList())
                : List.of();
    
        List<Long> screenIds = dto.getScreenIds() != null
                ? dto.getScreenIds().stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList())
                : List.of();
    
        Contract contract = Contract.builder()
                .info(dto.getInfo())
                .startContractAt(dto.getStartContractAt())
                .expiredAt(dto.getExpiredAt())
                .companyId(dto.getCompanyId())
                .supplyType(SupplyType.valueOf(dto.getSupplyType().toUpperCase()))
                .operatorType(OperatorType.valueOf(dto.getOperatorType().toUpperCase()))
                .accountName(dto.getAccountName())
                .durationType(ContractType.valueOf(dto.getDurationType().toUpperCase()))
                .contractValue(dto.getContractValue())
                .screenIds(screenIds)
                .accountPermissions(permissions)
                .build();
    
        return createContract(contract);
    }

    private AccountPermission mapToEntity(AccountPermissionDTO dto) {
        AccountPermission entity = new AccountPermission();
        entity.setName(dto.getName());
        entity.setCanEdit(dto.isCanEdit());
        entity.setCanRead(dto.isCanRead());
        return entity;
    }

    @Override
    public Contract getContractById(Long id) {
        log.info("Fetching contract with ID: {}", id);
        return contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + id));
    }

    @Override
    public List<Contract> getContractsByCompany(Long companyId) {
        log.info("Fetching contracts for company: {}", companyId);
        return contractRepository.findByCompanyId(companyId);
    }

    @Override
    public List<Contract> getContractsByScreen(Long screenId) {
        log.info("Fetching contracts for screen: {}", screenId);
        // Get all contracts and filter in Java for screen ID
        return contractRepository.findAll().stream()
                .filter(contract -> contract.getScreenIds() != null && 
                                  contract.getScreenIds().contains(screenId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Contract> getCurrentContractForScreen(Long screenId) {
        log.info("Fetching current contract for screen: {}", screenId);
        // Get all contracts and filter in Java, then get the most recent
        return contractRepository.findAll().stream()
                .filter(contract -> contract.getScreenIds() != null && 
                                  contract.getScreenIds().contains(screenId))
                .max(Comparator.comparing(Contract::getCreatedAt));
    }

    @Override
    public List<Contract> getContractsByCompanyName(String companyName) {
        log.info("Searching contracts by company name: {}", companyName);
        return contractRepository.findByCompanyNameContainingIgnoreCase(companyName);
    }

    @Override
    public double getTotalContractValue() {
        log.info("Calculating total contract value");
        Double result = contractRepository.sumAllContractValues();
        return result != null ? result : 0.0;
    }

    // NEW METHODS: Get screens by company through contracts
    @Override
    public List<ScreenResponse> getScreensByCompany(Long companyId) {
        log.info("Fetching screens for company: {}", companyId);
        List<Contract> contracts = contractRepository.findByCompanyId(companyId);
        
        return contracts.stream()
                .filter(contract -> contract.getScreenIds() != null) // Null safety
                .flatMap(contract -> contract.getScreenIds().stream())
                .distinct() // Remove duplicate screen IDs
                .map(screenId -> {
                    try {
                        return screenService.getScreenById(screenId);
                    } catch (Exception e) {
                        log.warn("Could not fetch screen with ID: {}, error: {}", screenId, e.getMessage());
                        return Optional.<ScreenResponse>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScreenResponse> getActiveScreensByCompany(Long companyId) {
        log.info("Fetching active screens for company: {}", companyId);
        List<Contract> activeContracts = contractRepository.findByCompanyId(companyId)
                .stream()
                .filter(contract -> contract.getExpiredAt().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        
        return activeContracts.stream()
                .filter(contract -> contract.getScreenIds() != null) // Null safety
                .flatMap(contract -> contract.getScreenIds().stream())
                .distinct() // Remove duplicate screen IDs
                .map(screenId -> {
                    try {
                        return screenService.getScreenById(screenId);
                    } catch (Exception e) {
                        log.warn("Could not fetch screen with ID: {}, error: {}", screenId, e.getMessage());
                        return Optional.<ScreenResponse>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getScreenIdsByCompany(Long companyId) {
        log.info("Fetching screen IDs for company: {}", companyId);
        List<Contract> contracts = contractRepository.findByCompanyId(companyId);
        
        return contracts.stream()
                .filter(contract -> contract.getScreenIds() != null) // Null safety
                .flatMap(contract -> contract.getScreenIds().stream())
                .distinct() // Remove duplicate screen IDs
                .collect(Collectors.toList());
    }

    @Override
    public Contract updateContract(Long id, Contract contractDetails) {
        log.info("Updating contract with ID: {}", id);
        Contract existingContract = getContractById(id);

        if (contractDetails.getInfo() != null) {
            existingContract.setInfo(contractDetails.getInfo());
        }
        if (contractDetails.getExpiredAt() != null) {
            existingContract.setExpiredAt(contractDetails.getExpiredAt());
        }
        if (contractDetails.getSupplyType() != null) {
            existingContract.setSupplyType(contractDetails.getSupplyType());
        }
        if (contractDetails.getOperatorType() != null) {
            existingContract.setOperatorType(contractDetails.getOperatorType());
        }
        if (contractDetails.getDurationType() != null) {
            existingContract.setDurationType(contractDetails.getDurationType());
        }
        if (contractDetails.getContractValue() != null) {
            existingContract.setContractValue(contractDetails.getContractValue());
        }
        if (contractDetails.getScreenIds() != null) {
            List<Long> validScreenIds = contractDetails.getScreenIds()
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            existingContract.setScreenIds(validScreenIds);
        }
        if (contractDetails.getAccountPermissions() != null) {
            existingContract.setAccountPermissions(contractDetails.getAccountPermissions());
        }

        Contract updatedContract = contractRepository.save(existingContract);
        log.info("Contract updated successfully with ID: {}", updatedContract.getId());
        return updatedContract;
    }

    @Override
    public void deleteContract(Long id) {
        log.info("Deleting contract with ID: {}", id);
        if (!contractRepository.existsById(id)) {
            throw new IllegalArgumentException("Contract not found with id: " + id);
        }
        contractRepository.deleteById(id);
        log.info("Contract deleted successfully with ID: {}", id);
    }

    @Override
    public boolean contractExistsForCompanyAndScreen(Long companyId, Long screenId) {
        log.debug("Checking if contract exists for company: {} and screen: {}", companyId, screenId);
        // Get contracts by company and check if any contains the screen ID
        return contractRepository.findByCompanyId(companyId).stream()
                .anyMatch(contract -> contract.getScreenIds() != null && 
                                    contract.getScreenIds().contains(screenId));
    }

    @Override
    public long getContractCountByMonthAndYear(int month, int year) {
        log.info("Getting contract count for month: {} and year: {}", month, year);
        return contractRepository.countByMonthAndYear(month, year);
    }

    @Override
    public List<MonthlyStats> getMonthlyContractStats() {
        log.info("Fetching monthly contract statistics");
        return contractRepository.getMonthlyContractRegistrationStats()
                .stream()
                .map(record -> new MonthlyStats(
                        ((Number) record[0]).intValue(),
                        ((Number) record[1]).intValue(),
                        ((Number) record[2]).longValue()))
                .toList();
    }

    @Override
    public List<AnnualStats> getAnnualContractStats() {
        log.info("Fetching annual contract statistics");
        return contractRepository.getAnnualContractRegistrationStats()
                .stream()
                .map(record -> new AnnualStats(
                        ((Number) record[0]).intValue(),
                        ((Number) record[1]).longValue()))
                .toList();
    }

    @Override
    public List<ContractResponseDTO> getAllContractsWithNames() {
        log.info("Fetching all contracts with names");
        List<Contract> contracts = contractRepository.findAll();
    
        return contracts.stream().map(contract -> {
            ContractResponseDTO dto = new ContractResponseDTO();
            dto.setId(contract.getId());
            dto.setInfo(contract.getInfo());
            dto.setStartContractAt(contract.getStartContractAt());
            dto.setExpiredAt(contract.getExpiredAt());
            dto.setAccountName(contract.getAccountName());
            dto.setContractValue(contract.getContractValue());
    
            // Convert enums to String safely
            dto.setDurationType(contract.getDurationType() != null ? contract.getDurationType().name() : null);
            dto.setOperatorType(contract.getOperatorType() != null ? contract.getOperatorType().name() : null);
            dto.setSupplyType(contract.getSupplyType() != null ? contract.getSupplyType().name() : null);
    
            dto.setCreatedAt(contract.getCreatedAt());
            dto.setUpdatedAt(contract.getUpdatedAt());
    
            // Map AccountPermission -> AccountPermissionDTO
            List<AccountPermissionDTO> permissionDTOs = contract.getAccountPermissions() != null
                    ? contract.getAccountPermissions().stream()
                        .map(permission -> {
                            AccountPermissionDTO dtoPermission = new AccountPermissionDTO();
                            dtoPermission.setName(permission.getName());
                            dtoPermission.setCanRead(permission.isCanRead());
                            dtoPermission.setCanEdit(permission.isCanEdit());
                            return dtoPermission;
                        }).collect(Collectors.toList())
                    : List.of();
    
            dto.setAccountPermissions(permissionDTOs);
    
            // Fetch company name safely
            try {
                companyRepository.findById(contract.getCompanyId())
                        .ifPresentOrElse(
                                company -> dto.setCompanyName(company.getName()),
                                () -> {
                                    log.warn("Company not found for ID: {}", contract.getCompanyId());
                                    dto.setCompanyName("Unknown Company");
                                }
                        );
            } catch (Exception e) {
                log.error("Error fetching company for ID: {}, error: {}", contract.getCompanyId(), e.getMessage());
                dto.setCompanyName("Unknown Company");
            }
    
            // Fetch screen names safely
            List<String> screenNames = contract.getScreenIds() != null
                    ? contract.getScreenIds().stream()
                        .map(screenId -> {
                            try {
                                return screenService.getScreenById(screenId)
                                        .map(ScreenResponse::getName)
                                        .orElse("Unknown Screen");
                            } catch (Exception e) {
                                log.warn("Error fetching screen for ID: {}, error: {}", screenId, e.getMessage());
                                return "Unknown Screen";
                            }
                        })
                        .collect(Collectors.toList())
                    : List.of();
    
            dto.setScreenNames(screenNames);
    
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<Contract> findAllPaginated(int page, int size) {
        log.info("Fetching contracts page: {} with size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return contractRepository.findAll(pageable);
    }

    @Override
    public Page<ContractResponseDTO> getAllContractsWithNamesPaginated(int page, int size) {
        log.info("Fetching contracts with names, page: {} with size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contract> contracts = contractRepository.findAll(pageable);
        
        return contracts.map(contract -> {
            ContractResponseDTO dto = new ContractResponseDTO();
            dto.setId(contract.getId());
            dto.setInfo(contract.getInfo());
            dto.setStartContractAt(contract.getStartContractAt());
            dto.setExpiredAt(contract.getExpiredAt());
            dto.setAccountName(contract.getAccountName());
            dto.setContractValue(contract.getContractValue());
            
            // Convert enums to String safely
            dto.setDurationType(contract.getDurationType() != null ? contract.getDurationType().name() : null);
            dto.setOperatorType(contract.getOperatorType() != null ? contract.getOperatorType().name() : null);
            dto.setSupplyType(contract.getSupplyType() != null ? contract.getSupplyType().name() : null);
            
            dto.setCreatedAt(contract.getCreatedAt());
            dto.setUpdatedAt(contract.getUpdatedAt());
            
            // Map AccountPermission -> AccountPermissionDTO
            List<AccountPermissionDTO> permissionDTOs = contract.getAccountPermissions() != null
                    ? contract.getAccountPermissions().stream()
                        .map(permission -> {
                            AccountPermissionDTO dtoPermission = new AccountPermissionDTO();
                            dtoPermission.setName(permission.getName());
                            dtoPermission.setCanRead(permission.isCanRead());
                            dtoPermission.setCanEdit(permission.isCanEdit());
                            return dtoPermission;
                        }).collect(Collectors.toList())
                    : List.of();
            
            dto.setAccountPermissions(permissionDTOs);
            
            // Fetch company name safely
            try {
                companyRepository.findById(contract.getCompanyId())
                        .ifPresentOrElse(
                                company -> dto.setCompanyName(company.getName()),
                                () -> {
                                    log.warn("Company not found for ID: {}", contract.getCompanyId());
                                    dto.setCompanyName("Unknown Company");
                                }
                        );
            } catch (Exception e) {
                log.error("Error fetching company for ID: {}, error: {}", contract.getCompanyId(), e.getMessage());
                dto.setCompanyName("Unknown Company");
            }
            
            // Fetch screen names safely
            List<String> screenNames = contract.getScreenIds() != null
                    ? contract.getScreenIds().stream()
                        .map(screenId -> {
                            try {
                                return screenService.getScreenById(screenId)
                                        .map(ScreenResponse::getName)
                                        .orElse("Unknown Screen");
                            } catch (Exception e) {
                                log.warn("Error fetching screen for ID: {}, error: {}", screenId, e.getMessage());
                                return "Unknown Screen";
                            }
                        })
                        .collect(Collectors.toList())
                    : List.of();
            
            dto.setScreenNames(screenNames);
            
            return dto;
        });
    }

    // Helper method to get active screen IDs (if needed elsewhere)
    public List<Long> getActiveContractScreenIds() {
        log.info("Fetching active contract screen IDs");
        return contractRepository.findActiveContracts(LocalDateTime.now()).stream()
                .filter(contract -> contract.getScreenIds() != null)
                .flatMap(contract -> contract.getScreenIds().stream())
                .distinct()
                .collect(Collectors.toList());
    }

@Override
public List<ScreenResponse> getActiveScreensByCompanyAndContracts(Long companyId, List<Long> contractIds) {
    log.info("Fetching active screens for company: {} and contracts: {}", companyId, contractIds);
    
    if (contractIds == null || contractIds.isEmpty()) {
        log.warn("No contract IDs provided for company: {}", companyId);
        return List.of();
    }
    
    List<Contract> activeContracts = contractIds.stream()
            .map(contractId -> {
                try {
                    return getContractById(contractId);
                } catch (IllegalArgumentException e) {
                    log.warn("Contract not found with ID: {}", contractId);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .filter(contract -> contract.getCompanyId().equals(companyId)) // Ensure contract belongs to the company
            .filter(contract -> contract.getExpiredAt().isAfter(LocalDateTime.now())) // Only active contracts
            .collect(Collectors.toList());
    
    if (activeContracts.isEmpty()) {
        log.info("No active contracts found for company: {} with provided contract IDs: {}", companyId, contractIds);
        return List.of();
    }
    
    return activeContracts.stream()
            .filter(contract -> contract.getScreenIds() != null) 
            .flatMap(contract -> contract.getScreenIds().stream())
            .distinct() 
            .map(screenId -> {
                try {
                    return screenService.getScreenById(screenId);
                } catch (Exception e) {
                    log.warn("Could not fetch screen with ID: {}, error: {}", screenId, e.getMessage());
                    return Optional.<ScreenResponse>empty();
                }
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
}

    // Additional helper method to get active contracts for a specific company
    public List<Contract> getActiveContractsByCompany(Long companyId) {
        log.info("Fetching active contracts for company: {}", companyId);
        return contractRepository.findByCompanyId(companyId).stream()
                .filter(contract -> contract.getExpiredAt().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }
}