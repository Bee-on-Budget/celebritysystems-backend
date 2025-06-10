package com.celebritysystems.service;

import com.celebritysystems.dto.TicketDTO;
import com.celebritysystems.dto.CreateTicketDTO;
import com.celebritysystems.dto.TicketResponseDTO;

import java.util.List;

public interface TicketService {
    List<TicketResponseDTO> getAllTickets();
    TicketResponseDTO getTicketById(Long id);
    TicketDTO createTicket(CreateTicketDTO ticketDTO) ;
    TicketDTO updateTicket(Long id, CreateTicketDTO updatedTicketDTO);
    void deleteTicket(Long id);
}