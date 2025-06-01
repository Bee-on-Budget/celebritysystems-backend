package com.celebritysystems.service.impl;

import com.celebritysystems.dto.TicketDTO;
import com.celebritysystems.dto.CreateTicketDTO;
import com.celebritysystems.entity.*;
import com.celebritysystems.entity.enums.TicketStatus;
import com.celebritysystems.repository.*;
import com.celebritysystems.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ScreenRepository screenRepository;
    private final CompanyRepository companyRepository;

    @Override
    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TicketDTO getTicketById(Long id) {
        return ticketRepository.findById(id)
                .map(this::toDTO)
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
            ticket.setStatus(TicketStatus.valueOf(updatedTicketDTO.getStatus()));
            return toDTO(ticketRepository.save(ticket));
        }).orElse(null);
    }

    @Override
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    private TicketDTO toDTO(Ticket ticket) {
        return TicketDTO.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .createdBy(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null)
                .assignedToWorkerId(ticket.getAssignedToWorker() != null ? ticket.getAssignedToWorker().getId() : null)
                .assignedBySupervisorId(ticket.getAssignedBySupervisor() != null ? ticket.getAssignedBySupervisor().getId() : null)
                .screenId(ticket.getScreen() != null ? ticket.getScreen().getId() : null)
                .status(ticket.getStatus() != null ? ticket.getStatus().name() : null)
                .createdAt(ticket.getCreatedAt())
                .companyId(ticket.getCompany() != null ? ticket.getCompany().getId() : null)
                .attachmentFileName(ticket.getAttachmentFileName())
                .build();
    }

    private Ticket toEntity(CreateTicketDTO dto) {
        User createdBy = dto.getCreatedBy() != null ? userRepository.findById(dto.getCreatedBy()).orElse(null) : null;
        User assignedTo = dto.getAssignedToWorkerId() != null ? userRepository.findById(dto.getAssignedToWorkerId()).orElse(null) : null;
        User assignedBy = dto.getAssignedBySupervisorId() != null ? userRepository.findById(dto.getAssignedBySupervisorId()).orElse(null) : null;
        Screen screen = dto.getScreenId() != null ? screenRepository.findById(dto.getScreenId()).orElse(null) : null;
        Company company = dto.getCompanyId() != null ? companyRepository.findById(dto.getCompanyId()).orElse(null) : null;

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
}
