package com.celebritysystems.service.impl;

import com.celebritysystems.dto.WorkerReportDTO;
import com.celebritysystems.dto.WorkerReportResponseDTO;
import com.celebritysystems.entity.Ticket;
import com.celebritysystems.entity.WorkerReport;
import com.celebritysystems.repository.TicketRepository;
import com.celebritysystems.repository.WorkerReportRepository;
import com.celebritysystems.service.WorkerReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkerReportServiceImpl implements WorkerReportService {

    private final WorkerReportRepository workerReportRepository;
    private final TicketRepository ticketRepository;

    @Override
    public WorkerReportResponseDTO createWorkerReport(Long ticketId, WorkerReportDTO workerReportDTO) {
        // Check if ticket exists
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + ticketId));

        // Check if report already exists for this ticket
        if (workerReportRepository.existsByTicketId(ticketId)) {
            throw new IllegalArgumentException("Worker report already exists for ticket ID: " + ticketId);
        }

        WorkerReport workerReport = toEntity(workerReportDTO, ticket);
        WorkerReport savedReport = workerReportRepository.save(workerReport);
        
        return toResponseDTO(savedReport);
    }

    @Override
    public WorkerReportResponseDTO getWorkerReportByTicketId(Long ticketId) {
        WorkerReport workerReport = workerReportRepository.findByTicketId(ticketId)
                .orElse(null);
        
        return workerReport != null ? toResponseDTO(workerReport) : null;
    }

    @Override
    public WorkerReportResponseDTO updateWorkerReport(Long ticketId, WorkerReportDTO workerReportDTO) {
        WorkerReport existingReport = workerReportRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Worker report not found for ticket ID: " + ticketId));

        updateEntityFromDTO(existingReport, workerReportDTO);
        WorkerReport updatedReport = workerReportRepository.save(existingReport);
        
        return toResponseDTO(updatedReport);
    }

    @Override
    public void deleteWorkerReport(Long ticketId) {
        WorkerReport workerReport = workerReportRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Worker report not found for ticket ID: " + ticketId));
        
        workerReportRepository.delete(workerReport);
    }

    private WorkerReport toEntity(WorkerReportDTO dto, Ticket ticket) {
        WorkerReportDTO.ReportData reportData = dto.getReport();
        WorkerReportDTO.ChecklistData checklist = reportData.getChecklist();

        // Parse the date string to LocalDateTime
        LocalDateTime reportDate = null;
        if (reportData.getDate() != null) {
            try {
                // Assuming date format is "yyyy-MM-dd"
                LocalDate date = LocalDate.parse(reportData.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                reportDate = date.atStartOfDay();
            } catch (Exception e) {
                reportDate = LocalDateTime.now();
            }
        }

        WorkerReport.ServiceType serviceType = null;
        if (reportData.getServiceType() != null) {
            try {
                serviceType = WorkerReport.ServiceType.valueOf(
                    reportData.getServiceType().toUpperCase().replace(" ", "_")
                );
            } catch (IllegalArgumentException e) {
                serviceType = WorkerReport.ServiceType.REGULAR_SERVICE;
            }
        }

        return WorkerReport.builder()
                .ticket(ticket)
                .reportDate(reportDate)
                .serviceType(serviceType)
                .dataCables(checklist.getDataCables())
                .powerCable(checklist.getPowerCable())
                .powerSupplies(checklist.getPowerSupplies())
                .ledModules(checklist.getLedModules())
                .coolingSystems(checklist.getCoolingSystems())
                .serviceLights(checklist.getServiceLights())
                .operatingComputers(checklist.getOperatingComputers())
                .software(checklist.getSoftware())
                .powerDBs(checklist.getPowerDBs())
                .mediaConverters(checklist.getMediaConverters())
                .controlSystems(checklist.getControlSystems())
                .videoProcessors(checklist.getVideoProcessors())
                .dateTime(reportData.getDateTime())
                .defectsFound(reportData.getDefectsFound())
                .solutionsProvided(reportData.getSolutionsProvided())
                .serviceSupervisorSignatures(reportData.getServiceSupervisorSignatures())
                .technicianSignatures(reportData.getTechnicianSignatures())
                .authorizedPersonSignatures(reportData.getAuthorizedPersonSignatures())
                .solutionImage(reportData.getSolutionImage())
                .build();
    }

    private void updateEntityFromDTO(WorkerReport entity, WorkerReportDTO dto) {
        WorkerReportDTO.ReportData reportData = dto.getReport();
        WorkerReportDTO.ChecklistData checklist = reportData.getChecklist();

        // Update report date
        if (reportData.getDate() != null) {
            try {
                LocalDate date = LocalDate.parse(reportData.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                entity.setReportDate(date.atStartOfDay());
            } catch (Exception e) {
                // Keep existing date if parsing fails
            }
        }

        // Update service type
        if (reportData.getServiceType() != null) {
            try {
                WorkerReport.ServiceType serviceType = WorkerReport.ServiceType.valueOf(
                    reportData.getServiceType().toUpperCase().replace(" ", "_")
                );
                entity.setServiceType(serviceType);
            } catch (IllegalArgumentException e) {
                // Keep existing service type if parsing fails
            }
        }

        // Update checklist fields
        if (checklist != null) {
            entity.setDataCables(checklist.getDataCables());
            entity.setPowerCable(checklist.getPowerCable());
            entity.setPowerSupplies(checklist.getPowerSupplies());
            entity.setLedModules(checklist.getLedModules());
            entity.setCoolingSystems(checklist.getCoolingSystems());
            entity.setServiceLights(checklist.getServiceLights());
            entity.setOperatingComputers(checklist.getOperatingComputers());
            entity.setSoftware(checklist.getSoftware());
            entity.setPowerDBs(checklist.getPowerDBs());
            entity.setMediaConverters(checklist.getMediaConverters());
            entity.setControlSystems(checklist.getControlSystems());
            entity.setVideoProcessors(checklist.getVideoProcessors());
        }

        // Update other fields
        entity.setDateTime(reportData.getDateTime());
        entity.setDefectsFound(reportData.getDefectsFound());
        entity.setSolutionsProvided(reportData.getSolutionsProvided());
        entity.setServiceSupervisorSignatures(reportData.getServiceSupervisorSignatures());
        entity.setTechnicianSignatures(reportData.getTechnicianSignatures());
        entity.setAuthorizedPersonSignatures(reportData.getAuthorizedPersonSignatures());
        entity.setSolutionImage(reportData.getSolutionImage());
    }

    private WorkerReportResponseDTO toResponseDTO(WorkerReport workerReport) {
        Map<String, String> checklist = new HashMap<>();
        checklist.put("Data Cables (Cat6/RJ45)", workerReport.getDataCables());
        checklist.put("Power Cable", workerReport.getPowerCable());
        checklist.put("Power Supplies", workerReport.getPowerSupplies());
        checklist.put("LED Modules", workerReport.getLedModules());
        checklist.put("Cooling Systems", workerReport.getCoolingSystems());
        checklist.put("Service Lights & Sockets", workerReport.getServiceLights());
        checklist.put("Operating Computers", workerReport.getOperatingComputers());
        checklist.put("Software", workerReport.getSoftware());
        checklist.put("Power DBs", workerReport.getPowerDBs());
        checklist.put("Media Converters", workerReport.getMediaConverters());
        checklist.put("Control Systems", workerReport.getControlSystems());
        checklist.put("Video Processors", workerReport.getVideoProcessors());

        return WorkerReportResponseDTO.builder()
                .id(workerReport.getId())
                .ticketId(workerReport.getTicket().getId())
                .reportDate(workerReport.getReportDate())
                .serviceType(workerReport.getServiceType() != null ? workerReport.getServiceType().getDisplayName() : null)
                .checklist(checklist)
                .dateTime(workerReport.getDateTime())
                .defectsFound(workerReport.getDefectsFound())
                .solutionsProvided(workerReport.getSolutionsProvided())
                .serviceSupervisorSignatures(workerReport.getServiceSupervisorSignatures())
                .technicianSignatures(workerReport.getTechnicianSignatures())
                .authorizedPersonSignatures(workerReport.getAuthorizedPersonSignatures())
                .solutionImage(workerReport.getSolutionImage())
                .createdAt(workerReport.getCreatedAt())
                .updatedAt(workerReport.getUpdatedAt())
                .build();
    }
}