package com.celebritysystems.controller;

import com.celebritysystems.dto.Reports.*;
import com.celebritysystems.service.ReportingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reporting")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(@Valid @RequestBody ReportingRequestDTO request) {
        log.info("Received request to generate report: {}", request);
        try {
            ReportingResponseDTO response = reportingService.generateReport(request);
            log.info("Successfully generated report for type: {}", request.getReportType());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to generate report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("REPORT_GENERATION_ERROR", 
                    "Failed to generate report: " + e.getMessage())
            );
        }
    }

    @GetMapping("/components/summary")
    public ResponseEntity<?> getComponentChangesSummary(
            @RequestParam(required = false) List<Long> screenIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<String> components) {
        
        log.info("Getting component changes summary for screens: {}, date range: {} to {}", 
                screenIds, startDate, endDate);
        
        try {
            List<ComponentChangesSummaryDTO> summaries = reportingService.getComponentChangesSummary(
                screenIds, startDate, endDate, components);
            log.info("Successfully retrieved component changes summary with {} components", summaries.size());
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            log.error("Failed to get component changes summary: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("SUMMARY_RETRIEVAL_ERROR", 
                    "Failed to retrieve component changes summary: " + e.getMessage())
            );
        }
    }

    @GetMapping("/components/detailed")
    public ResponseEntity<?> getDetailedChangeRecords(
            @RequestParam(required = false) List<Long> screenIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<String> components) {
        
        log.info("Getting detailed change records for screens: {}, date range: {} to {}", 
                screenIds, startDate, endDate);
        
        try {
            List<DetailedChangeRecordDTO> records = reportingService.getDetailedChangeRecords(
                screenIds, startDate, endDate, components);
            log.info("Successfully retrieved {} detailed change records", records.size());
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            log.error("Failed to get detailed change records: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("DETAILED_RECORDS_ERROR", 
                    "Failed to retrieve detailed change records: " + e.getMessage())
            );
        }
    }

    @GetMapping("/screens/history")
    public ResponseEntity<?> getScreenHistory(
            @RequestParam(required = false) List<Long> screenIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting screen history for screens: {}, date range: {} to {}", 
                screenIds, startDate, endDate);
        
        try {
            List<ScreenHistoryDTO> histories = reportingService.getScreenHistory(screenIds, startDate, endDate);
            log.info("Successfully retrieved screen history for {} screens", histories.size());
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            log.error("Failed to get screen history: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("SCREEN_HISTORY_ERROR", 
                    "Failed to retrieve screen history: " + e.getMessage())
            );
        }
    }

    @GetMapping("/components/{componentName}")
    public ResponseEntity<?> getComponentSpecificReport(
            @PathVariable String componentName,
            @RequestParam(required = false) List<Long> screenIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting component-specific report for: {} across screens: {}", componentName, screenIds);
        
        try {
            ComponentChangesSummaryDTO report = reportingService.getComponentSpecificReport(
                componentName, screenIds, startDate, endDate);
            log.info("Successfully retrieved component-specific report for: {}", componentName);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Failed to get component-specific report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("COMPONENT_SPECIFIC_ERROR", 
                    "Failed to retrieve component-specific report: " + e.getMessage())
            );
        }
    }

    @GetMapping("/count/total")
    public ResponseEntity<?> getTotalChangeCount(
            @RequestParam(required = false) List<Long> screenIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<String> components) {
        
        log.info("Getting total change count for screens: {}, date range: {} to {}", 
                screenIds, startDate, endDate);
        
        try {
            Long totalCount = reportingService.getTotalChangeCount(screenIds, startDate, endDate, components);
            log.info("Successfully retrieved total change count: {}", totalCount);
            return ResponseEntity.ok(Map.of("totalChanges", totalCount));
        } catch (Exception e) {
            log.error("Failed to get total change count: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("COUNT_RETRIEVAL_ERROR", 
                    "Failed to retrieve total change count: " + e.getMessage())
            );
        }
    }

    // Quick access endpoints for common queries

    @GetMapping("/screens/{screenId}/component-changes")
    public ResponseEntity<?> getSingleScreenComponentChanges(
            @PathVariable Long screenId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting component changes for single screen: {} from {} to {}", screenId, startDate, endDate);
        
        try {
            List<ComponentChangesSummaryDTO> summaries = reportingService.getComponentChangesSummary(
                List.of(screenId), startDate, endDate, null);
            log.info("Successfully retrieved component changes for screen: {}", screenId);
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            log.error("Failed to get component changes for screen {}: {}", screenId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("SCREEN_COMPONENT_ERROR", 
                    "Failed to retrieve component changes for screen: " + e.getMessage())
            );
        }
    }

    @GetMapping("/components/{componentName}/screens/{screenId}")
    public ResponseEntity<?> getComponentForSpecificScreen(
            @PathVariable String componentName,
            @PathVariable Long screenId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting component {} changes for screen: {} from {} to {}", 
                componentName, screenId, startDate, endDate);
        
        try {
            ComponentChangesSummaryDTO report = reportingService.getComponentSpecificReport(
                componentName, List.of(screenId), startDate, endDate);
            log.info("Successfully retrieved component {} changes for screen: {}", componentName, screenId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Failed to get component {} changes for screen {}: {}", 
                    componentName, screenId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("COMPONENT_SCREEN_ERROR", 
                    "Failed to retrieve component changes for screen: " + e.getMessage())
            );
        }
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<?> getDashboardSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting dashboard summary from {} to {}", startDate, endDate);
        
        try {
            ReportingRequestDTO request = ReportingRequestDTO.builder()
                    .reportType(ReportingRequestDTO.ReportType.SUMMARY)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();
            
            ReportingResponseDTO response = reportingService.generateReport(request);
            log.info("Successfully retrieved dashboard summary");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get dashboard summary: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("DASHBOARD_ERROR", 
                    "Failed to retrieve dashboard summary: " + e.getMessage())
            );
        }
    }

    @GetMapping("/components/available")
    public ResponseEntity<?> getAvailableComponents() {
        log.info("Getting list of available components for reporting");
        try {
            List<String> components = List.of(
                "Data Cables (Cat6/RJ45)",
                "Power Cable", 
                "Power Supplies",
                "LED Modules",
                "Cooling Systems",
                "Service Lights & Sockets",
                "Operating Computers",
                "Software",
                "Power DBs",
                "Media Converters",
                "Control Systems",
                "Video Processors"
            );
            return ResponseEntity.ok(Map.of("availableComponents", components));
        } catch (Exception e) {
            log.error("Failed to get available components: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("COMPONENTS_LIST_ERROR", 
                    "Failed to retrieve available components: " + e.getMessage())
            );
        }
    }

    // Error Response class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ErrorResponse {
        private String errorCode;
        private String message;
    }
}