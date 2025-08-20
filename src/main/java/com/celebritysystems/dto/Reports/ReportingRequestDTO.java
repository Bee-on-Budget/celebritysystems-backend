package com.celebritysystems.dto.Reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportingRequestDTO {
    private List<Long> screenIds; 
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> components; 
    private ReportType reportType;
    
    public enum ReportType {
        SUMMARY, 
        DETAILED, 
        COMPONENT_SPECIFIC 
    }
}
