package com.celebritysystems.service;

import com.celebritysystems.dto.statistics.DailyActivityResponseDTO;
import com.celebritysystems.dto.statistics.DailyActivityStatsDTO;
import com.celebritysystems.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyActivityService {

    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final TicketRepository ticketRepository;
    private final CompanyRepository companyRepository;

    public DailyActivityResponseDTO getDailyActivityStats(LocalDate startDate, LocalDate endDate) {
        log.info("Getting daily activity stats from {} to {}", startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get daily stats for each entity
        Map<LocalDate, Long> userStats = getDailyUserStats(startDateTime, endDateTime);
        Map<LocalDate, Long> contractStats = getDailyContractStats(startDateTime, endDateTime);
        Map<LocalDate, Long> ticketStats = getDailyTicketStats(startDateTime, endDateTime);
        Map<LocalDate, Long> companyStats = getDailyCompanyStats(startDateTime, endDateTime);
        
        // Generate all dates in range
        List<LocalDate> allDates = generateDateRange(startDate, endDate);
        
        // Build daily stats
        List<DailyActivityStatsDTO> dailyStats = allDates.stream()
                .map(date -> {
                    long usersCreated = userStats.getOrDefault(date, 0L);
                    long contractsCreated = contractStats.getOrDefault(date, 0L);
                    long ticketsCreated = ticketStats.getOrDefault(date, 0L);
                    long companiesCreated = companyStats.getOrDefault(date, 0L);
                    long totalActivity = usersCreated + contractsCreated + ticketsCreated + companiesCreated;
                    
                    return DailyActivityStatsDTO.builder()
                            .date(date)
                            .usersCreated(usersCreated)
                            .contractsCreated(contractsCreated)
                            .ticketsCreated(ticketsCreated)
                            .companiesCreated(companiesCreated)
                            .totalActivity(totalActivity)
                            .build();
                })
                .collect(Collectors.toList());
        
        // Calculate totals
        long totalUsersCreated = userStats.values().stream().mapToLong(Long::longValue).sum();
        long totalContractsCreated = contractStats.values().stream().mapToLong(Long::longValue).sum();
        long totalTicketsCreated = ticketStats.values().stream().mapToLong(Long::longValue).sum();
        long totalCompaniesCreated = companyStats.values().stream().mapToLong(Long::longValue).sum();
        long totalActivity = totalUsersCreated + totalContractsCreated + totalTicketsCreated + totalCompaniesCreated;
        
        return DailyActivityResponseDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .dailyStats(dailyStats)
                .totalUsersCreated(totalUsersCreated)
                .totalContractsCreated(totalContractsCreated)
                .totalTicketsCreated(totalTicketsCreated)
                .totalCompaniesCreated(totalCompaniesCreated)
                .totalActivity(totalActivity)
                .build();
    }
    
    private Map<LocalDate, Long> getDailyUserStats(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Object[]> results = userRepository.getDailyUserCreationStats(startDateTime, endDateTime);
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((java.sql.Date) result[0]).toLocalDate(),
                        result -> ((Number) result[1]).longValue()
                ));
    }
    
    private Map<LocalDate, Long> getDailyContractStats(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Object[]> results = contractRepository.getDailyContractCreationStats(startDateTime, endDateTime);
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((java.sql.Date) result[0]).toLocalDate(),
                        result -> ((Number) result[1]).longValue()
                ));
    }
    
    private Map<LocalDate, Long> getDailyTicketStats(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Object[]> results = ticketRepository.getDailyTicketCreationStats(startDateTime, endDateTime);
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((java.sql.Date) result[0]).toLocalDate(),
                        result -> ((Number) result[1]).longValue()
                ));
    }
    
    private Map<LocalDate, Long> getDailyCompanyStats(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Object[]> results = companyRepository.getDailyCompanyCreationStats(startDateTime, endDateTime);
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((java.sql.Date) result[0]).toLocalDate(),
                        result -> ((Number) result[1]).longValue()
                ));
    }
    
    private List<LocalDate> generateDateRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }
}

