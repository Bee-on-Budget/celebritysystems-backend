package com.celebritysystems.service;

import com.celebritysystems.dto.TicketDTO;
import com.celebritysystems.dto.CreateTicketDTO;
import com.celebritysystems.dto.TicketResponseDTO;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

public interface TicketService {
    List<TicketResponseDTO> getAllTickets();
    TicketResponseDTO getTicketById(Long id);
    TicketDTO createTicket(CreateTicketDTO ticketDTO) ;
    TicketDTO updateTicket(Long id, CreateTicketDTO updatedTicketDTO);
    void deleteTicket(Long id);
    List<TicketResponseDTO> getTicketsByWorkerName(String workerName);
    long countTicketsAssignedToWorker(String username);
    Page<TicketResponseDTO> getAllTicketsPaginated(int page, int size);
    long countTicketsCompletedByWorker(String username);
    Long getTicketsCount();
    Map<String, Long> getTicketCountByStatus();

    List<TicketResponseDTO> getTicketsByCompanyId(Long companyId);
}