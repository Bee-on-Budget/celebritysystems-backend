package com.celebritysystems.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyActivityStatsDTO {
    private LocalDate date;
    private long usersCreated;
    private long contractsCreated;
    private long ticketsCreated;
    private long companiesCreated;
    private long totalActivity;
}
