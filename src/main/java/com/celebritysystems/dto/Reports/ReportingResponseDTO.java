package com.celebritysystems.dto.Reports;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportingResponseDTO {
    private ReportingRequestDTO.ReportType reportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Long> screenIds;
    private List<ComponentChangesSummaryDTO> componentSummaries;
    private List<DetailedChangeRecordDTO> detailedRecords;
    private Map<String, Object> totalCounts;
}
