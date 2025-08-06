package com.celebritysystems.service.impl;

import com.celebritysystems.dto.TicketDTO;
import com.celebritysystems.dto.CreateTicketDTO;
import com.celebritysystems.dto.TicketResponseDTO;
import com.celebritysystems.dto.WorkerReportResponseDTO;
import com.celebritysystems.entity.*;
import com.celebritysystems.entity.enums.TicketStatus;
import com.celebritysystems.repository.*;
import com.celebritysystems.service.TicketService;
import com.celebritysystems.service.WorkerReportService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ScreenRepository screenRepository;
    private final CompanyRepository companyRepository;
    private final WorkerReportService workerReportService;

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
                byte[] fileBytes = ticketDTO.getFile().getBytes();
                ticket.setAttachmentFileName(ticketDTO.getFile().getOriginalFilename());
            } catch (IOException e) {
                throw new RuntimeException("Failed to process file upload", e);
            }
        }

        return toDTO(ticketRepository.save(ticket));
    }

    @Override
    public TicketDTO updateTicket(Long id, CreateTicketDTO updatedTicketDTO) {
        return ticketRepository.findById(id).map(ticket -> {
            ticket.setTitle(updatedTicketDTO.getTitle());
            ticket.setDescription(updatedTicketDTO.getDescription());

            if (updatedTicketDTO.getStatus() != null) {
                ticket.setStatus(TicketStatus.valueOf(updatedTicketDTO.getStatus()));
            }

            // Update assigned worker if provided
            if (updatedTicketDTO.getAssignedToWorkerId() != null) {
                User assignedWorker = userRepository.findById(updatedTicketDTO.getAssignedToWorkerId()).orElse(null);
                ticket.setAssignedToWorker(assignedWorker);
                ticket.setStatus(TicketStatus.IN_PROGRESS);
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

            return toDTO(ticketRepository.save(ticket));
        }).orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + id));
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

        return Ticket.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? TicketStatus.valueOf(dto.getStatus()) : null)
                .createdBy(createdBy)
                .assignedToWorker(assignedTo)
                .assignedBySupervisor(assignedBy)
                .screen(screen)
                .company(company)
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
                .build();
    }

    @Override
    public List<TicketResponseDTO> getPendingTickets() {
        List<Ticket> pendingTickets = ticketRepository.findByAssignedToWorkerIsNullAndAssignedBySupervisorIsNull();
        return pendingTickets.stream()
                .map(this::toTicketResponseDto)
                .collect(Collectors.toList());
    }

}