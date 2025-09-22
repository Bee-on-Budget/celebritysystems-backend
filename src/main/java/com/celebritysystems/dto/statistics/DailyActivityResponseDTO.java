package com.celebritysystems.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyActivityResponseDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyActivityStatsDTO> dailyStats;
    private long totalUsersCreated;
    private long totalContractsCreated;
    private long totalTicketsCreated;
    private long totalCompaniesCreated;
    private long totalActivity;
}

