package com.celebritysystems.service.impl;

import com.celebritysystems.dto.Reports.*;
import com.celebritysystems.entity.WorkerReport;
import com.celebritysystems.repository.WorkerReportRepository;
import com.celebritysystems.service.ReportingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingServiceImpl implements ReportingService {

    private final WorkerReportRepository workerReportRepository;

    // Component name mappings for consistency - FIXED: Added all components
    private static final Map<String, String> COMPONENT_FIELD_MAP = Map.ofEntries(
            Map.entry("Data Cables (Cat6/RJ45)", "dataCables"),
            Map.entry("Power Cable", "powerCable"),
            Map.entry("Power Supplies", "powerSupplies"),
            Map.entry("LED Modules", "ledModules"),
            Map.entry("Cooling Systems", "coolingSystems"),
            Map.entry("Service Lights & Sockets", "serviceLights"),
            Map.entry("Operating Computers", "operatingComputers"),
            Map.entry("Software", "software"),
            Map.entry("Power DBs", "powerDBs"),
            Map.entry("Media Converters", "mediaConverters"),
            Map.entry("Control Systems", "controlSystems"),
            Map.entry("Video Processors", "videoProcessors"));

    @Override
    public ReportingResponseDTO generateReport(ReportingRequestDTO request) {
        log.info("Generating report for request: {}", request);

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        List<Long> screenIds = request.getScreenIds();
        List<String> components = request.getComponents();

        ReportingResponseDTO.ReportingResponseDTOBuilder responseBuilder = ReportingResponseDTO.builder()
                .reportType(request.getReportType())
                .startDate(startDate)
                .endDate(endDate)
                .screenIds(screenIds);

        switch (request.getReportType()) {
            case SUMMARY:
                List<ComponentChangesSummaryDTO> summaries = getComponentChangesSummary(screenIds, startDate, endDate,
                        components);
                Map<String, Object> totalCounts = calculateTotalCounts(summaries);
                responseBuilder
                        .componentSummaries(summaries)
                        .totalCounts(totalCounts);
                break;

            case DETAILED:
                List<DetailedChangeRecordDTO> detailedRecords = getDetailedChangeRecords(screenIds, startDate, endDate,
                        components);
                responseBuilder.detailedRecords(detailedRecords);
                break;

            case COMPONENT_SPECIFIC:
                if (components != null && !components.isEmpty()) {
                    List<ComponentChangesSummaryDTO> componentSpecific = components.stream()
                            .map(component -> getComponentSpecificReport(component, screenIds, startDate, endDate))
                            .collect(Collectors.toList());
                    responseBuilder.componentSummaries(componentSpecific);
                }
                break;
        }

        return responseBuilder.build();
    }

    @Override
    public List<ComponentChangesSummaryDTO> getComponentChangesSummary(
            List<Long> screenIds, LocalDate startDate, LocalDate endDate, List<String> components) {

        log.info("Getting component changes summary for screens: {}, date range: {} to {}", screenIds, startDate, endDate);

        List<WorkerReport> reports = getWorkerReports(screenIds, startDate, endDate);
        List<String> targetComponents = components != null && !components.isEmpty()
                ? components
                : new ArrayList<>(COMPONENT_FIELD_MAP.keySet());

        Map<String, ComponentChangesSummaryDTO> summaryMap = new HashMap<>();

        for (String componentName : targetComponents) {
            ComponentChangesSummaryDTO summary = ComponentChangesSummaryDTO.builder()
                    .componentName(componentName)
                    .totalChanges(0L)
                    .changesPerScreen(new HashMap<>())
                    .changeTypeDistribution(new HashMap<>())
                    .build();
            summaryMap.put(componentName, summary);
        }

        reports.sort(Comparator.comparing(WorkerReport::getReportDate));

        for (WorkerReport report : reports) {
            // FIXED: Get the actual screen ID from the ticket's screen, not the ticket ID
            Long screenId = report.getTicket().getScreen() != null ? 
                           report.getTicket().getScreen().getId() : 
                           null;
            
            // Skip if no screen is associated
            if (screenId == null) {
                log.warn("Worker report {} has no associated screen, skipping", report.getId());
                continue;
            }

            for (String componentName : targetComponents) {
                String currentValue = getComponentValue(report, componentName);

                if (!"OK".equalsIgnoreCase(currentValue)) {
                    ComponentChangesSummaryDTO summary = summaryMap.get(componentName);
                    summary.setTotalChanges(summary.getTotalChanges() + 1);

                    // Update changes per screen
                    Long currentCount = summary.getChangesPerScreen().getOrDefault(screenId, 0L);
                    summary.getChangesPerScreen().put(screenId, currentCount + 1);

                    // Update change type distribution (just count by value)
                    String changeType = currentValue;
                    Long typeCount = summary.getChangeTypeDistribution().getOrDefault(changeType, 0L);
                    summary.getChangeTypeDistribution().put(changeType, typeCount + 1);
                }
            }
        }

        return new ArrayList<>(summaryMap.values());
    }

    @Override
    public List<DetailedChangeRecordDTO> getDetailedChangeRecords(
            List<Long> screenIds, LocalDate startDate, LocalDate endDate, List<String> components) {

        log.info("Getting detailed change records for screens: {}, date range: {} to {}", screenIds, startDate, endDate);

        List<WorkerReport> reports = getWorkerReports(screenIds, startDate, endDate);
        List<String> targetComponents = components != null && !components.isEmpty()
                ? components
                : new ArrayList<>(COMPONENT_FIELD_MAP.keySet());

        List<DetailedChangeRecordDTO> detailedRecords = new ArrayList<>();
        reports.sort(Comparator.comparing(WorkerReport::getReportDate));

        for (WorkerReport report : reports) {
            // FIXED: Get the actual screen ID from the ticket's screen, not the ticket ID
            Long screenId = report.getTicket().getScreen() != null ? 
                           report.getTicket().getScreen().getId() : 
                           null;
            
            // Skip if no screen is associated
            if (screenId == null) {
                log.warn("Worker report {} has no associated screen, skipping", report.getId());
                continue;
            }

            for (String componentName : targetComponents) {
                String currentValue = getComponentValue(report, componentName);

                if (!"OK".equalsIgnoreCase(currentValue)) {
                    DetailedChangeRecordDTO record = DetailedChangeRecordDTO.builder()
                            .ticketId(report.getTicket().getId())
                            .screenId(screenId) // Now using the correct screen ID
                            .componentName(componentName)
                            .previousValue(null) // You can fill this if you want, but not needed for just counting
                            .currentValue(currentValue)
                            .changeDate(report.getReportDate())
                            .build();

                    detailedRecords.add(record);
                }
            }
        }

        return detailedRecords;
    }

    @Override
    public List<ScreenHistoryDTO> getScreenHistory(List<Long> screenIds, LocalDate startDate, LocalDate endDate) {
        log.info("Getting screen history for screens: {}, date range: {} to {}", screenIds, startDate, endDate);

        List<WorkerReport> reports = getWorkerReports(screenIds, startDate, endDate);
        
        // FIXED: Group by actual screen ID, not ticket ID
        Map<Long, List<WorkerReport>> reportsByScreen = reports.stream()
                .filter(report -> report.getTicket().getScreen() != null) // Filter out reports without screens
                .collect(Collectors.groupingBy(report -> report.getTicket().getScreen().getId()));

        List<ScreenHistoryDTO> screenHistories = new ArrayList<>();

        for (Map.Entry<Long, List<WorkerReport>> entry : reportsByScreen.entrySet()) {
            Long screenId = entry.getKey();
            List<WorkerReport> screenReports = entry.getValue();
            screenReports.sort(Comparator.comparing(WorkerReport::getReportDate));

            // Get the screen name from the first report (assuming all reports have the same screen)
            String screenName = screenReports.get(0).getTicket().getScreen() != null ? 
                               screenReports.get(0).getTicket().getScreen().getName() : 
                               "Screen " + screenId;

            List<ComponentHistoryDTO> componentHistories = buildComponentHistories(screenReports);
            Long totalChanges = componentHistories.stream()
                    .mapToLong(ComponentHistoryDTO::getChangeCount)
                    .sum();

            ScreenHistoryDTO screenHistory = ScreenHistoryDTO.builder()
                    .screenId(screenId)
                    .screenName(screenName) // Using actual screen name
                    .componentHistories(componentHistories)
                    .totalChanges(totalChanges)
                    .build();

            screenHistories.add(screenHistory);
        }

        return screenHistories;
    }

    @Override
    public ComponentChangesSummaryDTO getComponentSpecificReport(
            String componentName, List<Long> screenIds, LocalDate startDate, LocalDate endDate) {

        log.info("Getting component-specific report for: {} across screens: {}", componentName, screenIds);

        return getComponentChangesSummary(screenIds, startDate, endDate, Arrays.asList(componentName))
                .stream()
                .filter(summary -> summary.getComponentName().equals(componentName))
                .findFirst()
                .orElse(ComponentChangesSummaryDTO.builder()
                        .componentName(componentName)
                        .totalChanges(0L)
                        .changesPerScreen(new HashMap<>())
                        .changeTypeDistribution(new HashMap<>())
                        .build());
    }

    @Override
    public Long getTotalChangeCount(List<Long> screenIds, LocalDate startDate, LocalDate endDate,
            List<String> components) {
        List<ComponentChangesSummaryDTO> summaries = getComponentChangesSummary(screenIds, startDate, endDate,
                components);
        return summaries.stream()
                .mapToLong(ComponentChangesSummaryDTO::getTotalChanges)
                .sum();
    }

    // Helper methods

    private List<WorkerReport> getWorkerReports(List<Long> screenIds, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        if (screenIds == null || screenIds.isEmpty()) {
            return workerReportRepository.findByReportDateBetween(startDateTime, endDateTime);
        } else {
            // FIXED: This method should filter by the screen ID associated with the ticket
            // You'll need to add this method to your WorkerReportRepository
            return workerReportRepository.findByTicketScreenIdInAndReportDateBetween(screenIds, startDateTime, endDateTime);
        }
    }

    private String getComponentValue(WorkerReport report, String componentName) {
        // Use switch statement to get the value based on component name
        switch (componentName) {
            case "Data Cables (Cat6/RJ45)":
                return report.getDataCables();
            case "Power Cable":
                return report.getPowerCable();
            case "Power Supplies":
                return report.getPowerSupplies();
            case "LED Modules":
                return report.getLedModules();
            case "Cooling Systems":
                return report.getCoolingSystems();
            case "Service Lights & Sockets":
                return report.getServiceLights();
            case "Operating Computers":
                return report.getOperatingComputers();
            case "Software":
                return report.getSoftware();
            case "Power DBs":
                return report.getPowerDBs();
            case "Media Converters":
                return report.getMediaConverters();
            case "Control Systems":
                return report.getControlSystems();
            case "Video Processors":
                return report.getVideoProcessors();
            default:
                return "OK";
        }
    }

    private List<ComponentHistoryDTO> buildComponentHistories(List<WorkerReport> screenReports) {
        Map<String, ComponentHistoryDTO> componentHistoryMap = new HashMap<>();

        for (String componentName : COMPONENT_FIELD_MAP.keySet()) {
            ComponentHistoryDTO componentHistory = ComponentHistoryDTO.builder()
                    .componentName(componentName)
                    .changeCount(0L)
                    .changes(new ArrayList<>())
                    .build();
            componentHistoryMap.put(componentName, componentHistory);
        }

        for (WorkerReport report : screenReports) {
            for (String componentName : COMPONENT_FIELD_MAP.keySet()) {
                String currentValue = getComponentValue(report, componentName);

                if (!"OK".equalsIgnoreCase(currentValue)) {
                    ComponentChangeEventDTO changeEvent = ComponentChangeEventDTO.builder()
                            .changeDate(report.getReportDate())
                            .fromValue(null) // You can fill this if you want, but not needed for just counting
                            .toValue(currentValue)
                            .ticketId(report.getTicket().getId())
                            .build();

                    ComponentHistoryDTO componentHistory = componentHistoryMap.get(componentName);
                    componentHistory.getChanges().add(changeEvent);
                    componentHistory.setChangeCount(componentHistory.getChangeCount() + 1);
                }
            }
        }

        return new ArrayList<>(componentHistoryMap.values());
    }

    private Map<String, Object> calculateTotalCounts(List<ComponentChangesSummaryDTO> summaries) {
        Map<String, Object> totalCounts = new HashMap<>();

        Long overallTotal = summaries.stream()
                .mapToLong(ComponentChangesSummaryDTO::getTotalChanges)
                .sum();

        totalCounts.put("overallTotal", overallTotal);

        Map<String, Long> componentTotals = summaries.stream()
                .collect(Collectors.toMap(
                        ComponentChangesSummaryDTO::getComponentName,
                        ComponentChangesSummaryDTO::getTotalChanges));

        totalCounts.put("componentTotals", componentTotals);

        return totalCounts;
    }
}