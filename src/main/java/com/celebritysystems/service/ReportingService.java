package com.celebritysystems.service;


import com.celebritysystems.dto.Reports.ComponentChangesSummaryDTO;
import com.celebritysystems.dto.Reports.DetailedChangeRecordDTO;
import com.celebritysystems.dto.Reports.ReportingRequestDTO;
import com.celebritysystems.dto.Reports.ReportingResponseDTO;
import com.celebritysystems.dto.Reports.ScreenHistoryDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportingService {
    
    /**
     * Generate comprehensive report based on request criteria
     */
    ReportingResponseDTO generateReport(ReportingRequestDTO request);
    
    /**
     * Get component changes summary for specific screens and date range
     */
    List<ComponentChangesSummaryDTO> getComponentChangesSummary(
            List<Long> screenIds, 
            LocalDate startDate, 
            LocalDate endDate,
            List<String> components
    );
    
    /**
     * Get detailed change records for audit trail
     */
    List<DetailedChangeRecordDTO> getDetailedChangeRecords(
            List<Long> screenIds,
            LocalDate startDate,
            LocalDate endDate,
            List<String> components
    );
    
    /**
     * Get screen history with all component changes
     */
    List<ScreenHistoryDTO> getScreenHistory(
            List<Long> screenIds,
            LocalDate startDate,
            LocalDate endDate
    );
    
    /**
     * Get component-specific report across all or specific screens
     */
    ComponentChangesSummaryDTO getComponentSpecificReport(
            String componentName,
            List<Long> screenIds,
            LocalDate startDate,
            LocalDate endDate
    );
    
    /**
     * Get total change counts for dashboard/summary view
     */
    Long getTotalChangeCount(
            List<Long> screenIds,
            LocalDate startDate,
            LocalDate endDate,
            List<String> components
    );
}