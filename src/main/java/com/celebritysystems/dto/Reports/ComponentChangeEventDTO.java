package com.celebritysystems.dto.Reports;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentChangeEventDTO {
    private LocalDateTime changeDate;
    private String fromValue;
    private String toValue;
    private Long ticketId;
    private String workerName;
}