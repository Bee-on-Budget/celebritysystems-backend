package com.celebritysystems.controller;

import com.celebritysystems.dto.TicketDTO;
import com.celebritysystems.dto.CreateTicketDTO;
import com.celebritysystems.dto.PatchTicketDTO;
import com.celebritysystems.dto.PatchWorkerReportDTO;
import com.celebritysystems.dto.TicketAnalyticsDTO;
import com.celebritysystems.dto.TicketAnalyticsSummaryDTO;
import com.celebritysystems.dto.TicketResponseDTO;
import com.celebritysystems.dto.WorkerReportDTO;
import com.celebritysystems.dto.WorkerReportResponseDTO;
import com.celebritysystems.entity.enums.ServiceType;
import com.celebritysystems.service.TicketService;
import com.celebritysystems.service.WorkerReportService;
import com.celebritysystems.service.S3Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final WorkerReportService workerReportService;
    private final S3Service s3Service;

    // ==================== TICKET ENDPOINTS ====================

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets() {
        log.info("Received request to get all tickets");
        try {
            List<TicketResponseDTO> tickets = ticketService.getAllTickets();
            log.info("Successfully retrieved {} tickets", tickets.size());
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            log.error("Failed to retrieve tickets: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/worker/{username}")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByWorkerName(@PathVariable String username) {
        log.info("Received request to getTicketsByWorkerName with username: {}", username);
        try {
            List<TicketResponseDTO> tickets = ticketService.getTicketsByWorkerName(username);
            if (tickets.isEmpty()) {
                log.warn("No tickets found for worker: {}", username);
                return ResponseEntity.ok(tickets);
            }
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            log.error("Failed to get tickets for worker {}: {}", username, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable Long id) {
        log.info("Received request to get ticket by ID: {}", id);
        try {
            TicketResponseDTO ticket = ticketService.getTicketById(id);
            if (ticket != null) {
                log.info("Successfully retrieved ticket with ID: {}", id);
                return ResponseEntity.ok(ticket);
            } else {
                log.warn("Ticket not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to retrieve ticket with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/worker/{username}/count")
    public ResponseEntity<?> getTicketCountsForWorker(@PathVariable String username) {
        log.info("Received request to get ticket counts for worker: {}", username);
        try {
            long assignedCount = ticketService.countTicketsAssignedToWorker(username);
            long completedCount = ticketService.countTicketsCompletedByWorker(username);
            Map<String, Long> response = new HashMap<>();
            response.put("assignedCount", assignedCount);
            response.put("completedCount", completedCount);
            log.info("Successfully retrieved ticket counts for worker: {}", username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get ticket counts for worker {}: {}", username, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createTicket(@Valid @ModelAttribute CreateTicketDTO ticketDTO) {
        log.info("Received request to create new ticket with service type: {}", ticketDTO.getServiceType());
        try {
            log.debug("Ticket creation payload: {}", ticketDTO.toString());

            // Validate service type if provided
            if (ticketDTO.getServiceType() != null) {
                try {
                    ServiceType.valueOf(ticketDTO.getServiceType());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(
                        new ErrorResponse("INVALID_SERVICE_TYPE", 
                            "Invalid service type. Valid values are: " + 
                            Arrays.toString(ServiceType.values())));
                }
            }

            TicketDTO createdTicket = ticketService.createTicket(ticketDTO);
            log.info("Successfully created ticket with ID: {}", createdTicket.getId());

            return ResponseEntity.ok(createdTicket);
        } catch (MultipartException e) {
            log.error("Multipart file processing error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("FILE_PROCESSING_ERROR",
                            "Error processing file upload: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.error("Validation error in ticket creation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during ticket creation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("INTERNAL_SERVER_ERROR",
                            "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/statistic/status-counts")
    public ResponseEntity<Map<String, Long>> getTicketCountsByStatus() {
        log.info("Received request to get ticket counts by status");
        try {
            Map<String, Long> statusCounts = ticketService.getTicketCountByStatus();
            log.info("Successfully retrieved ticket counts by status");
            return ResponseEntity.ok(statusCounts);
        } catch (Exception e) {
            log.error("Failed to retrieve ticket counts by status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable Long id, @Valid @RequestBody CreateTicketDTO ticketDTO) {
        log.info("Received request to update ticket with ID: {}", id);
        try {
            log.debug("Ticket update payload for ID {}: {}", id, ticketDTO.toString());

            TicketDTO updatedTicket = ticketService.updateTicket(id, ticketDTO);
            log.info("Successfully updated ticket with ID: {}", id);

            return ResponseEntity.ok(updatedTicket);
        } catch (IllegalArgumentException e) {
            log.error("Validation error in ticket update: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during ticket update: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("INTERNAL_SERVER_ERROR",
                            "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long id) {
        log.info("Received request to delete ticket with ID: {}", id);
        try {
            ticketService.deleteTicket(id);
            log.info("Successfully deleted ticket with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting ticket: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error during ticket deletion: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("INTERNAL_SERVER_ERROR",
                            "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("statistic/total")
    public ResponseEntity<Long> getTicketsCount() {
        log.info("Received request to get total tickets count");
        try {
            Long count = ticketService.getTicketsCount();
            log.info("Successfully retrieved total tickets count: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Failed to retrieve total tickets count: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<TicketResponseDTO>> getAllTicketsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching tickets page {} with size {}", page, size);
        try {
            Page<TicketResponseDTO> tickets = ticketService.getAllTicketsPaginated(page, size);
            log.info("Successfully retrieved page {} of tickets with {} items", page, tickets.getNumberOfElements());
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            log.error("Failed to retrieve paginated tickets: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByCompanyId(@PathVariable Long companyId) {
        log.info("Received request to getTicketsByCompanyId with Id: {}", companyId);
        try {
            List<TicketResponseDTO> tickets = ticketService.getTicketsByCompanyId(companyId);
            if (tickets.isEmpty()) {
                log.warn("No tickets found for company with id: {}", companyId);
                return ResponseEntity.ok(tickets);
            }
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            log.error("Failed to get tickets for company id {}: {}", companyId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==================== WORKER REPORT ENDPOINTS ====================

    @PostMapping(path = "/{ticketId}/worker-report", consumes = "multipart/form-data")
    public ResponseEntity<?> createWorkerReport(
            @PathVariable Long ticketId,
            @ModelAttribute WorkerReportDTO workerReportDTO) throws JsonProcessingException {

        log.info("Received request to create worker report for ticket ID: {}", ticketId);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            WorkerReportDTO.ChecklistData checklistData = objectMapper.readValue(workerReportDTO.getChecklist(),
                    WorkerReportDTO.ChecklistData.class);

            WorkerReportResponseDTO createdReport = workerReportService.createWorkerReport(ticketId, workerReportDTO,
                    checklistData);
            log.info("Successfully created worker report for ticket ID: {}", ticketId);
            return ResponseEntity.ok(createdReport);
        } catch (IllegalArgumentException e) {
            log.error("Validation error in worker report creation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during worker report creation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("INTERNAL_SERVER_ERROR",
                            "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/{ticketId}/worker-report")
    public ResponseEntity<?> getWorkerReportByTicketId(@PathVariable Long ticketId) {
        log.info("Received request to get worker report for ticket ID: {}", ticketId);
        try {
            WorkerReportResponseDTO workerReport = workerReportService.getWorkerReportByTicketId(ticketId);
            if (workerReport != null) {
                log.info("Successfully retrieved worker report for ticket ID: {}", ticketId);
                return ResponseEntity.ok(workerReport);
            } else {
                log.warn("Worker report not found for ticket ID: {}", ticketId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to retrieve worker report for ticket ID {}: {}", ticketId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("INTERNAL_SERVER_ERROR",
                            "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PutMapping("/{ticketId}/worker-report")
    public ResponseEntity<?> updateWorkerReport(
            @PathVariable Long ticketId,
            @Valid @RequestBody WorkerReportDTO workerReportDTO) {
        log.info("Received request to update worker report for ticket ID: {}", ticketId);
        try {
            WorkerReportResponseDTO updatedReport = workerReportService.updateWorkerReport(ticketId, workerReportDTO);
            log.info("Successfully updated worker report for ticket ID: {}", ticketId);
            return ResponseEntity.ok(updatedReport);
        } catch (IllegalArgumentException e) {
            log.error("Validation error in worker report update: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during worker report update: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("INTERNAL_SERVER_ERROR",
                            "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{ticketId}/worker-report")
    public ResponseEntity<?> deleteWorkerReport(@PathVariable Long ticketId) {
        log.info("Received request to delete worker report for ticket ID: {}", ticketId);
        try {
            workerReportService.deleteWorkerReport(ticketId);
            log.info("Successfully deleted worker report for ticket ID: {}", ticketId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting worker report: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error during worker report deletion: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("INTERNAL_SERVER_ERROR",
                            "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TicketResponseDTO>> getPendingTickets() {
        log.info("Received request to get all pending tickets");
        try {
            List<TicketResponseDTO> pendingTickets = ticketService.getPendingTickets();
            log.info("Successfully retrieved {} pending tickets", pendingTickets.size());
            return ResponseEntity.ok(pendingTickets);
        } catch (Exception e) {
            log.error("Failed to retrieve pending tickets: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==================== TICKET IMAGE DOWNLOAD ENDPOINT ====================

    @GetMapping("/{id}/image/download")
    public ResponseEntity<Resource> downloadTicketImage(@PathVariable Long id) {
        log.info("Downloading ticket image for ticket ID: {}", id);

        TicketResponseDTO ticket = ticketService.getTicketById(id);
        if (ticket == null || ticket.getTicketImageUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = s3Service.downloadFile(ticket.getTicketImageUrl());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + ticket.getTicketImageName() + "\"");

            String contentType = determineContentType(ticket.getTicketImageName());
            headers.setContentType(MediaType.parseMediaType(contentType));

            log.info("Ticket image downloaded successfully: {}", ticket.getTicketImageName());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            log.error("Failed to download ticket image for ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{ticketId}/worker-report")
    public ResponseEntity<?> patchWorkerReport(
            @PathVariable Long ticketId,
            @Valid @RequestBody PatchWorkerReportDTO patchWorkerReportDTO) {

        log.info("Received PATCH request to update worker report for ticket ID: {}", ticketId);

        try {
            log.debug("Worker report PATCH payload for ticket ID {}: {}", ticketId, patchWorkerReportDTO.toString());

            WorkerReportResponseDTO patchedReport = workerReportService.patchWorkerReport(ticketId,
                    patchWorkerReportDTO);
            log.info("Successfully patched worker report for ticket ID: {}", ticketId);

            return ResponseEntity.ok(patchedReport);

        } catch (IllegalArgumentException e) {
            log.error("Validation error in worker report patch: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during worker report patch: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("INTERNAL_SERVER_ERROR",
                            "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchTicket(@PathVariable Long id, @Valid @RequestBody PatchTicketDTO patchTicketDTO) {
        log.info("Received PATCH request to update ticket with ID: {}", id);
        try {
            log.debug("Ticket PATCH payload for ID {}: {}", id, patchTicketDTO.toString());

            TicketDTO updatedTicket = ticketService.patchTicket(id, patchTicketDTO);
            log.info("Successfully patched ticket with ID: {}", id);

            return ResponseEntity.ok(updatedTicket);
        } catch (IllegalArgumentException e) {
            log.error("Validation error in ticket patch: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during ticket patch: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("INTERNAL_SERVER_ERROR",
                            "An unexpected error occurred: " + e.getMessage()));
        }
    }

    // ==================== HELPER METHODS ====================

    private String determineContentType(String fileName) {
        if (fileName == null)
            return "application/octet-stream";

        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "txt" -> "text/plain";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "application/octet-stream";
        };
    }

    // ==================== ERROR RESPONSE CLASS ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ErrorResponse {
        private String errorCode;
        private String message;
    }

    @GetMapping("/service-types")
    public ResponseEntity<List<Map<String, String>>> getServiceTypes() {
        List<Map<String, String>> serviceTypes = Arrays.stream(ServiceType.values())
            .map(type -> {
                Map<String, String> typeInfo = new HashMap<>();
                typeInfo.put("name", type.name());
                typeInfo.put("displayName", type.getDisplayName());
                return typeInfo;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(serviceTypes);
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getTicketAnalytics(
            @RequestParam(required = false) List<Long> screenIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting ticket analytics for screens: {}, period: {} to {}", 
                screenIds, startDate, endDate);
        
        try {
            List<TicketAnalyticsDTO> analytics = 
                ticketService.getTicketAnalytics(screenIds, startDate, endDate);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Failed to get ticket analytics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("ANALYTICS_ERROR", 
                    "Failed to retrieve ticket analytics: " + e.getMessage())
            );
        }
    }

    @GetMapping("/analytics/summary")
    public ResponseEntity<?> getTicketAnalyticsSummary(
            @RequestParam(required = false) List<Long> screenIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting ticket analytics summary for screens: {}, period: {} to {}", 
                screenIds, startDate, endDate);
        
        try {
            TicketAnalyticsSummaryDTO summary = 
                ticketService.getTicketAnalyticsSummary(screenIds, startDate, endDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Failed to get ticket analytics summary: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("ANALYTICS_SUMMARY_ERROR", 
                    "Failed to retrieve ticket analytics summary: " + e.getMessage())
            );
        }
    }
}
