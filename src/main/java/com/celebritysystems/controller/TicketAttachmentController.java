package com.celebritysystems.controller;

import com.celebritysystems.dto.TicketAttachmentDTO;
import com.celebritysystems.service.TicketAttachmentService;
import com.celebritysystems.dto.CreateTicketAttachmentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class TicketAttachmentController {

    private final TicketAttachmentService ticketAttachmentService;

 
    @GetMapping("/{id}")
    public ResponseEntity<TicketAttachmentDTO> getAttachmentById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketAttachmentService.getAttachmentById(id));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<TicketAttachmentDTO> addAttachment(@ModelAttribute CreateTicketAttachmentDTO dto) {
        return ResponseEntity.ok(ticketAttachmentService.addAttachment(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        ticketAttachmentService.deleteAttachment(id);
        return ResponseEntity.noContent().build();
    }
}