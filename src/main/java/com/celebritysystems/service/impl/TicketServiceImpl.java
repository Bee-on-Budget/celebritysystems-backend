package com.celebritysystems.service.impl;

import com.celebritysystems.dto.TicketDTO;
import com.celebritysystems.dto.CreateTicketDTO;
import com.celebritysystems.dto.PatchTicketDTO;
import com.celebritysystems.dto.TicketAnalyticsDTO;
import com.celebritysystems.dto.TicketAnalyticsSummaryDTO;
import com.celebritysystems.dto.TicketResponseDTO;
import com.celebritysystems.dto.WorkerReportResponseDTO;
import com.celebritysystems.entity.*;
import com.celebritysystems.entity.enums.ServiceType;
import com.celebritysystems.entity.enums.TicketStatus;
import com.celebritysystems.repository.*;
import com.celebritysystems.service.S3Service;
import com.celebritysystems.service.TicketService;
import com.celebritysystems.service.WorkerReportService;
import com.celebritysystems.service.OneSignalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ScreenRepository screenRepository;
    private final CompanyRepository companyRepository;
    private final WorkerReportService workerReportService;
    private final OneSignalService oneSignalService; // Add OneSignal service
    private final S3Service s3Service;

    @Override
    public List<TicketResponseDTO> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(this::toTicketResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public TicketResponseDTO getTicketById(Long id) {
        return ticketRepository.findById(id)
                .map(this::toTicketResponseDto)
                .orElse(null);
    }

    @Override
    public TicketDTO createTicket(CreateTicketDTO ticketDTO) {
        Ticket ticket = toEntity(ticketDTO);
        ticket.setCreatedAt(LocalDateTime.now());

        if (ticketDTO.getFile() != null && !ticketDTO.getFile().isEmpty()) {
            try {
                // Upload the general attachment file to S3
                String fileUrl = s3Service.uploadFile(ticketDTO.getFile(), "ticket-attachments");
                ticket.setAttachmentFileName(ticketDTO.getFile().getOriginalFilename());
                // Note: You might want to store the S3 URL as well for downloads
                log.info("Uploaded ticket attachment file: {}", ticketDTO.getFile().getOriginalFilename());
            } catch (Exception e) {
                log.error("Failed to upload ticket attachment file", e);
                throw new RuntimeException("Failed to process file upload", e);
            }
        }
        
        ticket = updateTicketStatus(ticket, TicketStatus.OPEN);

        Ticket savedTicket = ticketRepository.save(ticket);

        // Send notification if ticket is assigned to a worker during creation
        if (savedTicket.getAssignedToWorker() != null) {
            sendTicketAssignmentNotification(savedTicket);
        }

        return toDTO(savedTicket);
    }

    @Override
    public TicketDTO updateTicket(Long id, CreateTicketDTO updatedTicketDTO) {
        return ticketRepository.findById(id).map(ticket -> {
            // Store the previous assigned worker and status to detect changes
            User previousAssignedWorker = ticket.getAssignedToWorker();
            TicketStatus previousStatus = ticket.getStatus();

            ticket.setTitle(updatedTicketDTO.getTitle());
            ticket.setDescription(updatedTicketDTO.getDescription());

            // Update status if provided
            TicketStatus newStatus = null;
            if (updatedTicketDTO.getStatus() != null) {
                newStatus = TicketStatus.valueOf(updatedTicketDTO.getStatus());
                ticket.setStatus(newStatus);
            }

            // Update assigned worker if provided
            User newAssignedWorker = null;
            if (updatedTicketDTO.getAssignedToWorkerId() != null) {
                newAssignedWorker = userRepository.findById(updatedTicketDTO.getAssignedToWorkerId()).orElse(null);
                ticket.setAssignedToWorker(newAssignedWorker);
                ticket.setStatus(TicketStatus.IN_PROGRESS);
                ticket = updateTicketStatus(ticket, TicketStatus.IN_PROGRESS);
            }

            // Update assigned supervisor if provided
            if (updatedTicketDTO.getAssignedBySupervisorId() != null) {
                User assignedSupervisor = userRepository.findById(updatedTicketDTO.getAssignedBySupervisorId())
                        .orElse(null);
                ticket.setAssignedBySupervisor(assignedSupervisor);
            }

            // Update screen if provided
            if (updatedTicketDTO.getScreenId() != null) {
                Screen screen = screenRepository.findById(updatedTicketDTO.getScreenId()).orElse(null);
                ticket.setScreen(screen);
            }

            // Update company if provided
            if (updatedTicketDTO.getCompanyId() != null) {
                Company company = companyRepository.findById(updatedTicketDTO.getCompanyId()).orElse(null);
                ticket.setCompany(company);
            }

            Ticket savedTicket = ticketRepository.save(ticket);

            // Send notification if worker assignment changed
            if (hasWorkerAssignmentChanged(previousAssignedWorker, newAssignedWorker)) {
                sendTicketAssignmentNotification(savedTicket);
            }

            // Send notification to all company users if status changed
            if (hasStatusChanged(previousStatus, newStatus)) {
                sendTicketStatusUpdateNotificationToCompany(savedTicket, previousStatus, newStatus);
            }

            return toDTO(savedTicket);
        }).orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + id));
    }

    /**
     * Check if the worker assignment has changed
     */
    private boolean hasWorkerAssignmentChanged(User previousWorker, User newWorker) {
        // If both are null, no change
        if (previousWorker == null && newWorker == null) {
            return false;
        }

        // If one is null and the other isn't, there's a change
        if (previousWorker == null || newWorker == null) {
            return true;
        }

        // If both exist, check if they're different
        return !previousWorker.getId().equals(newWorker.getId());
    }

    /**
     * Check if the ticket status has changed
     */
    private boolean hasStatusChanged(TicketStatus previousStatus, TicketStatus newStatus) {
        // If both are null, no change
        if (previousStatus == null && newStatus == null) {
            return false;
        }

        // If one is null and the other isn't, there's a change
        if (previousStatus == null || newStatus == null) {
            return true;
        }

        // If both exist, check if they're different
        return !previousStatus.equals(newStatus);
    }

    /**
     * Send notification to the assigned worker about the new ticket
     */
    private void sendTicketAssignmentNotification(Ticket ticket) {
        try {
            if (ticket.getAssignedToWorker() != null && ticket.getAssignedToWorker().getPlayerId() != null) {
                String playerId = ticket.getAssignedToWorker().getPlayerId();
                String workerName = ticket.getAssignedToWorker().getFullName();

                // Prepare notification content
                String title = "New Ticket Assigned";
                String message = String.format("Hi %s, a new ticket '%s' has been assigned to you.",
                        workerName, ticket.getTitle());

                // Prepare additional data to send with notification
                Map<String, Object> data = new HashMap<>();
                data.put("ticketId", ticket.getId().toString());
                data.put("ticketTitle", ticket.getTitle());
                data.put("ticketDescription", ticket.getDescription());
                data.put("ticketStatus", ticket.getStatus() != null ? ticket.getStatus().name() : "PENDING");
                data.put("assignedAt", LocalDateTime.now().toString());
                data.put("companyName", ticket.getCompany() != null ? ticket.getCompany().getName() : "");
                data.put("screenName", ticket.getScreen() != null ? ticket.getScreen().getName() : "");
                data.put("screenLocation", ticket.getScreen() != null ? ticket.getScreen().getLocation() : "");
                data.put("createdBy", ticket.getCreatedBy() != null ? ticket.getCreatedBy().getFullName() : "");
                data.put("notificationType", "TICKET_ASSIGNMENT");

                // Send notification to the specific user
                List<String> playerIds = List.of(playerId);
                oneSignalService.sendWithData(title, message, data, playerIds);

                log.info("Notification sent to worker {} (playerId: {}) for ticket ID: {}",
                        workerName, playerId, ticket.getId());

            } else {
                log.warn("Cannot send notification: Worker has no playerId for ticket ID: {}", ticket.getId());
            }
        } catch (Exception e) {
            log.error("Failed to send ticket assignment notification for ticket ID: {}", ticket.getId(), e);
            // Don't throw exception to avoid breaking the ticket assignment process
        }
    }

    /**
     * Send notification to all users in the company about ticket status update
     */
    private void sendTicketStatusUpdateNotificationToCompany(Ticket ticket, TicketStatus previousStatus, TicketStatus newStatus) {
        try {
            if (ticket.getCompany() == null) {
                log.warn("Cannot send company notification: Ticket {} has no associated company", ticket.getId());
                return;
            }

            // Get all users from the same company who have playerIds
            List<User> companyUsers = userRepository.findByCompanyIdAndPlayerIdIsNotNull(ticket.getCompany().getId());

            if (companyUsers.isEmpty()) {
                log.warn("No users with playerIds found for company {} for ticket {}",
                        ticket.getCompany().getName(), ticket.getId());
                return;
            }

            // Extract playerIds from company users
            List<String> playerIds = companyUsers.stream()
                    .map(User::getPlayerId)
                    .filter(playerId -> playerId != null && !playerId.trim().isEmpty())
                    .collect(Collectors.toList());

            if (playerIds.isEmpty()) {
                log.warn("No valid playerIds found for company users for ticket {}", ticket.getId());
                return;
            }

            // Prepare notification content
            String title = "Ticket Status Updated";
            String previousStatusStr = previousStatus != null ? previousStatus.name() : "No Status";
            String newStatusStr = newStatus != null ? newStatus.name() : "No Status";

            String message = String.format("Ticket '%s' status changed from %s to %s",
                    ticket.getTitle(), previousStatusStr, newStatusStr);

            // Prepare additional data to send with notification
            Map<String, Object> data = new HashMap<>();
            data.put("ticketId", ticket.getId().toString());
            data.put("ticketTitle", ticket.getTitle());
            data.put("ticketDescription", ticket.getDescription());
            data.put("previousStatus", previousStatusStr);
            data.put("newStatus", newStatusStr);
            data.put("updatedAt", LocalDateTime.now().toString());
            data.put("companyName", ticket.getCompany().getName());
            data.put("screenName", ticket.getScreen() != null ? ticket.getScreen().getName() : "");
            data.put("screenLocation", ticket.getScreen() != null ? ticket.getScreen().getLocation() : "");
            data.put("assignedWorker", ticket.getAssignedToWorker() != null ? ticket.getAssignedToWorker().getFullName() : "");
            data.put("notificationType", "TICKET_STATUS_UPDATE");

            // Send notification to all company users
            oneSignalService.sendWithData(title, message, data, playerIds);

            log.info("Status update notification sent to {} users in company '{}' for ticket ID: {}",
                    playerIds.size(), ticket.getCompany().getName(), ticket.getId());

        } catch (Exception e) {
            log.error("Failed to send ticket status update notification for ticket ID: {}", ticket.getId(), e);
            // Don't throw exception to avoid breaking the ticket update process
        }
    }

    @Override
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new IllegalArgumentException("Ticket not found with ID: " + id);
        }
        ticketRepository.deleteById(id);
    }

    @Override
    public List<TicketResponseDTO> getTicketsByWorkerName(String workerName) {
        List<Ticket> tickets = ticketRepository.findByAssignedToWorker_Username(workerName);
        return tickets.stream()
                .map(this::toTicketResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public long countTicketsAssignedToWorker(String username) {
        return ticketRepository.countByAssignedToWorker_Username(username);
    }

    @Override
    public long countTicketsCompletedByWorker(String username) {
        return ticketRepository.countByAssignedToWorker_UsernameAndStatus(username, TicketStatus.CLOSED);
    }

    @Override
    public Page<TicketResponseDTO> getAllTicketsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Ticket> tickets = ticketRepository.findAll(pageable);
        return tickets.map(this::toTicketResponseDto);
    }

    @Override
    public Long getTicketsCount() {
        return ticketRepository.count();
    }

    @Override
    public Map<String, Long> getTicketCountByStatus() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Map<String, Long> statusCounts = new HashMap<>();

        // Initialize with all possible statuses at 0
        Arrays.stream(TicketStatus.values())
                .forEach(status -> statusCounts.put(status.name(), 0L));

        // Add "NULL" status for tickets with no status
        statusCounts.put("NULL", 0L);

        // Get counts from last 30 days (excluding NULL statuses)
        List<Object[]> results = ticketRepository.countTicketsGroupByStatusSinceDate(thirtyDaysAgo);
        for (Object[] result : results) {
            TicketStatus status = (TicketStatus) result[0];
            Long count = (Long) result[1];
            statusCounts.put(status.name(), count);
        }

        // Count NULL status tickets separately
        Long nullStatusCount = ticketRepository.countByStatusIsNullAndCreatedAtAfter(thirtyDaysAgo);
        statusCounts.put("NULL", nullStatusCount);

        return statusCounts;
    }

    @Override
    public List<TicketResponseDTO> getTicketsByCompanyId(Long companyId) {
        List<Ticket> tickets = ticketRepository.findByCompanyId(companyId);
        return tickets.stream()
                .map(this::toTicketResponseDto)
                .collect(Collectors.toList());
    }

    private TicketDTO toDTO(Ticket ticket) {
        return TicketDTO.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .createdBy(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null)
                .assignedToWorkerId(ticket.getAssignedToWorker() != null ? ticket.getAssignedToWorker().getId() : null)
                .assignedBySupervisorId(
                        ticket.getAssignedBySupervisor() != null ? ticket.getAssignedBySupervisor().getId() : null)
                .screenId(ticket.getScreen() != null ? ticket.getScreen().getId() : null)
                .status(ticket.getStatus() != null ? ticket.getStatus().name() : null)
                .createdAt(ticket.getCreatedAt())
                .companyId(ticket.getCompany() != null ? ticket.getCompany().getId() : null)
                .attachmentFileName(ticket.getAttachmentFileName())
                .serviceType(ticket.getServiceType() != null ? ticket.getServiceType().name() : null)  // Add this line
                .build();
    }

    private Ticket toEntity(CreateTicketDTO dto) {
        User createdBy = dto.getCreatedBy() != null ? userRepository.findById(dto.getCreatedBy()).orElse(null) : null;
        User assignedTo = dto.getAssignedToWorkerId() != null
                ? userRepository.findById(dto.getAssignedToWorkerId()).orElse(null)
                : null;
        User assignedBy = dto.getAssignedBySupervisorId() != null
                ? userRepository.findById(dto.getAssignedBySupervisorId()).orElse(null)
                : null;
        Screen screen = dto.getScreenId() != null ? screenRepository.findById(dto.getScreenId()).orElse(null) : null;
        Company company = dto.getCompanyId() != null ? companyRepository.findById(dto.getCompanyId()).orElse(null)
                : null;

        // File Upload Fields
        String ticketImageUrl = null;
        String ticketImageName = null;
        if (dto.getTicketImage() != null && !dto.getTicketImage().isEmpty()) {
            ticketImageUrl = s3Service.uploadFile(dto.getTicketImage(), "ticket-files/ticket-image");
            ticketImageName = dto.getTicketImage().getOriginalFilename();
        }

        return Ticket.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? TicketStatus.valueOf(dto.getStatus()) : null)
                .createdBy(createdBy)
                .assignedToWorker(assignedTo)
                .assignedBySupervisor(assignedBy)
                .screen(screen)
                .company(company)
                .ticketImageUrl(ticketImageUrl)
                .ticketImageName(ticketImageName)
                .serviceType(dto.getServiceType() != null ? ServiceType.valueOf(dto.getServiceType()) : null)  // Add this line
                .build();
    }

    private TicketResponseDTO toTicketResponseDto(Ticket ticket) {
        // Get worker report if exists
        WorkerReportResponseDTO workerReport = null;
        try {
            workerReport = workerReportService.getWorkerReportByTicketId(ticket.getId());
        } catch (Exception e) {
            // Log warning but don't fail the ticket retrieval
            // log.warn("Failed to retrieve worker report for ticket {}: {}",
            // ticket.getId(), e.getMessage());
        }

        return TicketResponseDTO.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .createdBy(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null)
                .assignedToWorkerName(ticket.getAssignedToWorker() != null
                        ? ticket.getAssignedToWorker().getFullName()
                        : null)
                .assignedBySupervisorName(ticket.getAssignedBySupervisor() != null
                        ? ticket.getAssignedBySupervisor().getFullName()
                        : null)
                .screenName(ticket.getScreen() != null ? ticket.getScreen().getName() : null)
                .companyName(ticket.getCompany() != null ? ticket.getCompany().getName() : null)
                .status(ticket.getStatus() != null ? ticket.getStatus().name() : null)
                .createdAt(ticket.getCreatedAt())
                .attachmentFileName(ticket.getAttachmentFileName())
                .location(ticket.getScreen() != null ? ticket.getScreen().getLocation() : null)
                .screenType(ticket.getScreen() != null ? ticket.getScreen().getScreenType().toString() : null)
                .workerReport(workerReport) // Include worker report in response
                .openedAt(ticket.getOpenedAt())
                .inProgressAt(ticket.getInProgressAt())
                .resolvedAt(ticket.getResolvedAt())
                .closedAt(ticket.getClosedAt())
                .ticketImageUrl(ticket.getTicketImageUrl())
                .ticketImageName(ticket.getTicketImageName())
                .serviceType(ticket.getServiceType() != null ? ticket.getServiceType().name() : null)  // Add this line
                .serviceTypeDisplayName(ticket.getServiceType() != null ? ticket.getServiceType().getDisplayName() : null)  // Add this line
                .build();
    }

    @Override
    public List<TicketResponseDTO> getPendingTickets() {
        List<Ticket> pendingTickets = ticketRepository.findByAssignedToWorkerIsNullAndAssignedBySupervisorIsNull();
        return pendingTickets.stream()
                .map(this::toTicketResponseDto)
                .collect(Collectors.toList());
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
    @Override
public TicketDTO patchTicket(Long id, PatchTicketDTO patchTicketDTO) {
    return ticketRepository.findById(id).map(ticket -> {
        // Store the previous assigned worker and status to detect changes
        User previousAssignedWorker = ticket.getAssignedToWorker();
        TicketStatus previousStatus = ticket.getStatus();
        
        boolean hasChanges = false;

        // Update status if provided
        TicketStatus newStatus = null;
        if (patchTicketDTO.hasStatus()) {
            try {
                newStatus = TicketStatus.valueOf(patchTicketDTO.getStatus());
                ticket = updateTicketStatus(ticket, newStatus);
                hasChanges = true;
                log.info("Updated status to {} for ticket ID: {}", newStatus, id);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + patchTicketDTO.getStatus());
            }
        }

        // Update assigned worker if provided
        User newAssignedWorker = null;
        if (patchTicketDTO.hasAssignedToWorkerId()) {
            newAssignedWorker = userRepository.findById(patchTicketDTO.getAssignedToWorkerId())
                    .orElseThrow(() -> new IllegalArgumentException("Worker not found with ID: " + patchTicketDTO.getAssignedToWorkerId()));
            ticket.setAssignedToWorker(newAssignedWorker);
            
            // Auto-update status to IN_PROGRESS when assigning a worker (if status not explicitly provided)
            if (!patchTicketDTO.hasStatus()) {
                ticket = updateTicketStatus(ticket, TicketStatus.IN_PROGRESS);
                newStatus = TicketStatus.IN_PROGRESS;
                log.info("Auto-updated status to IN_PROGRESS when assigning worker for ticket ID: {}", id);
            }
            hasChanges = true;
            log.info("Assigned worker {} to ticket ID: {}", newAssignedWorker.getFullName(), id);
        }

        // Update assigned supervisor if provided
        if (patchTicketDTO.hasAssignedBySupervisorId()) {
            User assignedSupervisor = userRepository.findById(patchTicketDTO.getAssignedBySupervisorId())
                    .orElseThrow(() -> new IllegalArgumentException("Supervisor not found with ID: " + patchTicketDTO.getAssignedBySupervisorId()));
            ticket.setAssignedBySupervisor(assignedSupervisor);
            hasChanges = true;
            log.info("Assigned supervisor {} to ticket ID: {}", assignedSupervisor.getFullName(), id);
        }

        // Only save if there were actual changes
        if (!hasChanges) {
            log.info("No changes detected for ticket ID: {}", id);
            return toDTO(ticket);
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        // Send notifications only if relevant changes occurred
        if (hasWorkerAssignmentChanged(previousAssignedWorker, newAssignedWorker)) {
            sendTicketAssignmentNotification(savedTicket);
        }

        if (hasStatusChanged(previousStatus, newStatus)) {
            sendTicketStatusUpdateNotificationToCompany(savedTicket, previousStatus, newStatus);
        }

        log.info("Successfully patched ticket with ID: {}", id);
        return toDTO(savedTicket);

    }).orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + id));
}

    @Override
    public List<TicketAnalyticsDTO> getTicketAnalytics(List<Long> screenIds, 
                                                      LocalDate startDate, 
                                                      LocalDate endDate) {
        List<Ticket> tickets = getTicketsForAnalysis(screenIds, startDate, endDate);

        return tickets.stream()
            .map(ticket -> {
                Duration resolution = null;
                if (ticket.getOpenedAt() != null && ticket.getClosedAt() != null) {
                    resolution = Duration.between(ticket.getOpenedAt(), ticket.getClosedAt());
                }

                return TicketAnalyticsDTO.builder()
                    .ticketId(ticket.getId())
                    .serviceType(ticket.getServiceType() != null ? 
                               ticket.getServiceType().getDisplayName() : 
                               "Unspecified")
                    .resolutionTime(resolution)
                    .resolutionTimeFormatted(formatDuration(resolution))
                    .build();
            })
            .collect(Collectors.toList());
    }

    @Override
    public TicketAnalyticsSummaryDTO getTicketAnalyticsSummary(List<Long> screenIds, 
                                                              LocalDate startDate, 
                                                              LocalDate endDate) {
        List<Ticket> tickets = getTicketsForAnalysis(screenIds, startDate, endDate);
        
        // Calculate service type counts including null cases
        Map<String, Long> serviceTypeCounts = tickets.stream()
            .collect(Collectors.groupingBy(
                ticket -> ticket.getServiceType() != null ? 
                         ticket.getServiceType().getDisplayName() : 
                         "Unspecified",
                Collectors.counting()
            ));

        // Calculate average resolution time
        List<Duration> resolutionTimes = tickets.stream()
            .filter(ticket -> ticket.getOpenedAt() != null && ticket.getClosedAt() != null)
            .map(ticket -> Duration.between(ticket.getOpenedAt(), ticket.getClosedAt()))
            .collect(Collectors.toList());

        Duration averageResolution = calculateAverageDuration(resolutionTimes);

        // Calculate average time by service type handling null cases
        Map<String, Duration> averageTimeByServiceType = tickets.stream()
            .filter(ticket -> ticket.getOpenedAt() != null && ticket.getClosedAt() != null)
            .collect(Collectors.groupingBy(
                ticket -> ticket.getServiceType() != null ? 
                         ticket.getServiceType().getDisplayName() : 
                         "Unspecified",
                Collectors.collectingAndThen(
                    Collectors.mapping(
                        ticket -> Duration.between(ticket.getOpenedAt(), ticket.getClosedAt()),
                        Collectors.toList()
                    ),
                    this::calculateAverageDuration
                )
            ));

        return TicketAnalyticsSummaryDTO.builder()
            .averageResolutionTime(averageResolution)
            .averageResolutionTimeFormatted(formatDuration(averageResolution))
            .serviceTypeCounts(serviceTypeCounts)
            .averageTimeByServiceType(averageTimeByServiceType)
            .totalTickets((long) tickets.size())
            .build();
    }

    private List<Ticket> getTicketsForAnalysis(List<Long> screenIds, 
                                             LocalDate startDate, 
                                             LocalDate endDate) {
        if (screenIds != null && !screenIds.isEmpty()) {
            return ticketRepository.findByScreenIdInAndCreatedAtBetween(
                screenIds, 
                startDate.atStartOfDay(), 
                endDate.atTime(23, 59, 59)
            );
        }
        return ticketRepository.findByCreatedAtBetween(
            startDate.atStartOfDay(), 
            endDate.atTime(23, 59, 59)
        );
    }

    private Duration calculateAverageDuration(List<Duration> durations) {
        if (durations == null || durations.isEmpty()) {
            return Duration.ZERO;
        }
        
        long totalSeconds = durations.stream()
            .mapToLong(Duration::getSeconds)
            .sum();
            
        return Duration.ofSeconds(totalSeconds / durations.size());
    }

    private String formatDuration(Duration duration) {
        if (duration == null) {
            return "N/A";
        }
        
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        
        StringBuilder formatted = new StringBuilder();
        if (days > 0) {
            formatted.append(days).append(" days ");
        }
        if (hours > 0 || days > 0) {
            formatted.append(hours).append(" hours ");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            formatted.append(minutes).append(" minutes ");
        }
        if (seconds > 0 && days == 0 && hours == 0) { // Only show seconds if less than an hour
            formatted.append(seconds).append(" seconds");
        }
        
        return formatted.toString().trim();
    }
    @Override
public List<TicketResponseDTO> getTicketsWithWorkerReportsByScreenId(Long screenId) {
    log.info("Fetching tickets with worker reports for screen ID: {}", screenId);
    
    // Verify screen exists
    if (!screenRepository.existsById(screenId)) {
        throw new IllegalArgumentException("Screen not found with ID: " + screenId);
    }
    
    List<Ticket> tickets = ticketRepository.findByScreenId(screenId);
    
    return tickets.stream()
            .map(this::toTicketResponseDtoWithWorkerReport)
            .collect(Collectors.toList());
}
private TicketResponseDTO toTicketResponseDtoWithWorkerReport(Ticket ticket) {
    // Get worker report if exists
    WorkerReportResponseDTO workerReport = null;
    try {
        workerReport = workerReportService.getWorkerReportByTicketId(ticket.getId());
    } catch (Exception e) {
        log.warn("Failed to retrieve worker report for ticket {}: {}", 
                ticket.getId(), e.getMessage());
    }

    return TicketResponseDTO.builder()
            .id(ticket.getId())
            .title(ticket.getTitle())
            .description(ticket.getDescription())
            .createdBy(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null)
            .assignedToWorkerName(ticket.getAssignedToWorker() != null
                    ? ticket.getAssignedToWorker().getFullName()
                    : null)
            .assignedBySupervisorName(ticket.getAssignedBySupervisor() != null
                    ? ticket.getAssignedBySupervisor().getFullName()
                    : null)
            .screenName(ticket.getScreen() != null ? ticket.getScreen().getName() : null)
            .companyName(ticket.getCompany() != null ? ticket.getCompany().getName() : null)
            .status(ticket.getStatus() != null ? ticket.getStatus().name() : null)
            .createdAt(ticket.getCreatedAt())
            .attachmentFileName(ticket.getAttachmentFileName())
            .location(ticket.getScreen() != null ? ticket.getScreen().getLocation() : null)
            .screenType(ticket.getScreen() != null ? ticket.getScreen().getScreenType().toString() : null)
            .workerReport(workerReport) // This ensures worker report is always included
            .openedAt(ticket.getOpenedAt())
            .inProgressAt(ticket.getInProgressAt())
            .resolvedAt(ticket.getResolvedAt())
            .closedAt(ticket.getClosedAt())
            .ticketImageUrl(ticket.getTicketImageUrl())
            .ticketImageName(ticket.getTicketImageName())
            .serviceType(ticket.getServiceType() != null ? ticket.getServiceType().name() : null)
            .serviceTypeDisplayName(ticket.getServiceType() != null ? ticket.getServiceType().getDisplayName() : null)
            .build();
}
}
