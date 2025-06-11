package com.celebritysystems.controller;

import com.celebritysystems.dto.TicketDTO;
import com.celebritysystems.dto.CreateTicketDTO;
import com.celebritysystems.dto.TicketResponseDTO;
import com.celebritysystems.service.TicketService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

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
        try {
            List<TicketResponseDTO> tickets = ticketService.getTicketsByWorkerName(username);
            if (tickets.isEmpty()) {
                log.warn("No tickets found for worker: {}", username);
                return ResponseEntity.notFound().build();
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
    try {
        long assignedCount = ticketService.countTicketsAssignedToWorker(username);
        long completedCount = ticketService.countTicketsCompletedByWorker(username);
        Map<String, Long> response = new HashMap<>();
        response.put("assignedCount", assignedCount);
        response.put("completedCount", completedCount);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        log.error("Failed to get ticket counts for worker {}: {}", username, e.getMessage(), e);
        return ResponseEntity.internalServerError().build();
    }
}

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createTicket(@Valid @ModelAttribute CreateTicketDTO ticketDTO) {
        log.info("Received request to create new ticket");
        try {
            log.debug("Ticket creation payload: {}", ticketDTO.toString());
            
            TicketDTO createdTicket = ticketService.createTicket(ticketDTO);
            log.info("Successfully created ticket with ID: {}", createdTicket.getId());
            
            return ResponseEntity.ok(createdTicket);
        } catch (MultipartException e) {
            log.error("Multipart file processing error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                new ErrorResponse("FILE_PROCESSING_ERROR", 
                "Error processing file upload: " + e.getMessage())
            );
        } catch (IllegalArgumentException e) {
            log.error("Validation error in ticket creation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                new ErrorResponse("VALIDATION_ERROR", e.getMessage())
            );
        } catch (Exception e) {
            log.error("Unexpected error during ticket creation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("INTERNAL_SERVER_ERROR", 
                "An unexpected error occurred: " + e.getMessage())
            );
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
                new ErrorResponse("VALIDATION_ERROR", e.getMessage())
            );
        } catch (Exception e) {
            log.error("Unexpected error during ticket update: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse("INTERNAL_SERVER_ERROR", 
                "An unexpected error occurred: " + e.getMessage())
            );
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
                "An unexpected error occurred: " + e.getMessage())
            );
        }
    }

    @Data
    @NoArgsConstructor 
    @AllArgsConstructor
    private static class ErrorResponse {
        private String errorCode;
        private String message;
    }
}