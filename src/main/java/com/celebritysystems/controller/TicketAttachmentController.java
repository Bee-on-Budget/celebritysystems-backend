package com.celebritysystems.controller;

import com.celebritysystems.dto.TicketAttachmentDTO;
import com.celebritysystems.dto.CreateTicketAttachmentDTO;
import com.celebritysystems.service.TicketAttachmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
@Slf4j
public class TicketAttachmentController {

    private final TicketAttachmentService ticketAttachmentService;

    @GetMapping("/{id}")
    public ResponseEntity<TicketAttachmentDTO> getAttachmentById(@PathVariable Long id) {
        log.info("Fetching attachment with ID: {}", id);
        TicketAttachmentDTO attachment = ticketAttachmentService.getAttachmentById(id);
        log.debug("Fetched attachment: {}", attachment);
        return ResponseEntity.ok(attachment);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<TicketAttachmentDTO> addAttachment(@ModelAttribute CreateTicketAttachmentDTO dto) {
        log.info("Adding new ticket attachment for ticket ID: {}", dto.getTicketId());
        TicketAttachmentDTO savedAttachment = ticketAttachmentService.addAttachment(dto);
        log.debug("Saved attachment: {}", savedAttachment);
        return ResponseEntity.ok(savedAttachment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        log.warn("Deleting attachment with ID: {}", id);
        ticketAttachmentService.deleteAttachment(id);
        log.info("Attachment with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}