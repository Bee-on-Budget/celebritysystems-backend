package com.celebritysystems.service;

import com.celebritysystems.dto.TicketDTO;
import com.celebritysystems.dto.CreateTicketDTO;

import java.util.List;

public interface TicketService {
    List<TicketDTO> getAllTickets();
    TicketDTO getTicketById(Long id);
    TicketDTO createTicket(CreateTicketDTO ticketDTO);
    TicketDTO updateTicket(Long id, CreateTicketDTO updatedTicketDTO);
    void deleteTicket(Long id);
}