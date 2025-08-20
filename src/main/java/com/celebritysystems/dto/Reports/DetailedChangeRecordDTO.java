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
public class DetailedChangeRecordDTO {
    private Long ticketId;
    private Long screenId;
    private String componentName;
    private String previousValue;
    private String currentValue;
    private LocalDateTime changeDate;
    private String workerName;
}