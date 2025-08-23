package com.celebritysystems.service.impl;

import com.celebritysystems.dto.PatchWorkerReportDTO;
import com.celebritysystems.dto.WorkerReportDTO;
import com.celebritysystems.dto.WorkerReportResponseDTO;
import com.celebritysystems.entity.Ticket;
import com.celebritysystems.entity.WorkerReport;
import com.celebritysystems.entity.enums.TicketStatus;
import com.celebritysystems.repository.TicketRepository;
import com.celebritysystems.repository.WorkerReportRepository;
import com.celebritysystems.service.S3Service;
import com.celebritysystems.service.WorkerReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class WorkerReportServiceImpl implements WorkerReportService {

    private final WorkerReportRepository workerReportRepository;
    private final TicketRepository ticketRepository;
    private final S3Service s3Service;

    @Override
    public WorkerReportResponseDTO createWorkerReport(Long ticketId, WorkerReportDTO workerReportDTO, WorkerReportDTO.ChecklistData checklistData) {
        // Check if ticket exists
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + ticketId));

        // Check if report already exists for this ticket
        if (workerReportRepository.existsByTicketId(ticketId)) {
            throw new IllegalArgumentException("Worker report already exists for ticket ID: " + ticketId);
        }

        log.info("Received request to create worker report for ticket ID With workerReportDTO: {}", workerReportDTO);
        log.info("Received request to create worker report for ticket ID With SolutionsProvided: {}", workerReportDTO.getSolutionsProvided());

        WorkerReport workerReport = toEntity(workerReportDTO, ticket, checklistData);
        WorkerReport savedReport = workerReportRepository.save(workerReport);

        ticket.setStatus(TicketStatus.RESOLVED);
        ticket = updateTicketStatus(ticket, TicketStatus.RESOLVED);
        ticketRepository.save(ticket);

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

    private WorkerReport toEntity(WorkerReportDTO dto, Ticket ticket, WorkerReportDTO.ChecklistData checklist) {
        WorkerReportDTO reportData = dto;
//        WorkerReportDTO.ChecklistData checklist = reportData.getChecklist();


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

        // File Upload Fields
        String solutionImageUrl = null;
        String solutionImageName = null;

        String technicianSignaturesUrl = null;
        String technicianSignaturesName = null;

        if (dto.getSolutionImage() != null && !dto.getSolutionImage().isEmpty()) {
            solutionImageUrl = s3Service.uploadFile(dto.getSolutionImage(), "ticket-files/solution-image");
            solutionImageName = dto.getSolutionImage().getOriginalFilename();
        }

        if (dto.getTechnicianSignatures() != null && !dto.getTechnicianSignatures().isEmpty()) {
            technicianSignaturesUrl = s3Service.uploadFile(dto.getTechnicianSignatures(), "technician-signatures/signature-image");
            technicianSignaturesName = dto.getTechnicianSignatures().getOriginalFilename();
        }

        return WorkerReport.builder()
                .ticket(ticket)
                .reportDate(reportDate)
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
//                .serviceSupervisorSignatures(reportData.getServiceSupervisorSignatures().toString()) //TODO: remove .toString() and fix the logic
                .technicianSignatures(technicianSignaturesUrl)
                .technicianSignaturesName(technicianSignaturesName)
//                .authorizedPersonSignatures(reportData.getAuthorizedPersonSignatures().toString()) //TODO: remove .toString() and fix the logic
                .solutionImage(solutionImageUrl)
                .solutionImageName(solutionImageName)
                .build();
    }

    private void updateEntityFromDTO(WorkerReport entity, WorkerReportDTO dto) {
        WorkerReportDTO reportData = dto;
//        WorkerReportDTO.ChecklistData checklist = reportData.getChecklist();
        WorkerReportDTO.ChecklistData checklist = new WorkerReportDTO.ChecklistData(); //TODO : this object should got it from request.
        // Update report date
        if (reportData.getDate() != null) {
            try {
                LocalDate date = LocalDate.parse(reportData.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                entity.setReportDate(date.atStartOfDay());
            } catch (Exception e) {
                // Keep existing date if parsing fails
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
//        entity.setServiceSupervisorSignatures(reportData.getServiceSupervisorSignatures().toString()); //TODO: remove .toString() and fix the logic
//        entity.setTechnicianSignatures(reportData.getTechnicianSignatures().toString()); //TODO: Maybe you should somethings more here to delete the old Image from S3service for example? so get the name(should be uniq) and them remove it from the service in somehow, and remove toString
//        entity.setAuthorizedPersonSignatures(reportData.getAuthorizedPersonSignatures().toString()); //TODO: remove .toString() and fix the logic
//        entity.setSolutionImage(reportData.getSolutionImage().toString()); //TODO: Maybe you should somethings more here to delete the old Image from S3service for example? so get the name(should be uniq) and them remove it from the service in somehow, and remove toString
    }

    @Override
public WorkerReportResponseDTO patchWorkerReport(Long ticketId, PatchWorkerReportDTO patchWorkerReportDTO) {
    log.info("Patching worker report for ticket ID: {}", ticketId);
    
    WorkerReport existingReport = workerReportRepository.findByTicketId(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Worker report not found for ticket ID: " + ticketId));

    // Only update fields that are not null in the patch DTO
    if (patchWorkerReportDTO.getDefectsFound() != null) {
        existingReport.setDefectsFound(patchWorkerReportDTO.getDefectsFound());
        log.debug("Updated defectsFound for ticket ID: {}", ticketId);
    }
    
    if (patchWorkerReportDTO.getSolutionsProvided() != null) {
        existingReport.setSolutionsProvided(patchWorkerReportDTO.getSolutionsProvided());
        log.debug("Updated solutionsProvided for ticket ID: {}", ticketId);
    }

    WorkerReport updatedReport = workerReportRepository.save(existingReport);
    log.info("Successfully patched worker report for ticket ID: {}", ticketId);
    
    return toResponseDTO(updatedReport);
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
                // Remove serviceType from response
                //.serviceType(workerReport.getServiceType() != null ? workerReport.getServiceType().getDisplayName() : null)
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

    public Ticket updateTicketStatus(Ticket ticket, TicketStatus newStatus) {
        ticket.setStatus(newStatus);
        switch (newStatus) {
            case OPEN:
                ticket.setOpenedAt(LocalDateTime.now());
                break;
            case IN_PROGRESS:
                ticket.setInProgressAt(LocalDateTime.now());
                break;
            case RESOLVED:
                ticket.setResolvedAt(LocalDateTime.now());
                break;
            case CLOSED:
                ticket.setClosedAt(LocalDateTime.now());
                break;
        }
        return ticket;
    }
}
