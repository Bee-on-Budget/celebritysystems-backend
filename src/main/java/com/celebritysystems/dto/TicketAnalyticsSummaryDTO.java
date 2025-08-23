package com.celebritysystems.dto;

import java.time.Duration;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAnalyticsSummaryDTO {
    private Duration averageResolutionTime;
    private String averageResolutionTimeFormatted;
    private Map<String, Long> serviceTypeCounts;
    private Map<String, Duration> averageTimeByServiceType;
    private Long totalTickets;
}