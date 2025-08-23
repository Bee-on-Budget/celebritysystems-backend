package com.celebritysystems.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAnalyticsDTO {
    private Long ticketId;
    private String serviceType;
    private Duration resolutionTime;  // Time between OPEN and CLOSE
    private String resolutionTimeFormatted; // Human readable format
}

