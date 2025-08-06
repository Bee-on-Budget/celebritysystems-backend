package com.celebritysystems.controller;

import com.celebritysystems.dto.TicketAttachmentDTO;
import com.celebritysystems.dto.CreateTicketAttachmentDTO;
import com.celebritysystems.service.TicketAttachmentService;
import com.celebritysystems.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
@Slf4j
public class TicketAttachmentController {

    private final TicketAttachmentService ticketAttachmentService;
    private final S3Service s3Service;

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

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id) {
        log.info("Downloading attachment with ID: {}", id);
        
        TicketAttachmentDTO attachment = ticketAttachmentService.getAttachmentById(id);
        if (attachment == null || attachment.getFileUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = s3Service.downloadFile(attachment.getFileUrl());
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + attachment.getFileName() + "\"");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            log.info("Attachment downloaded successfully: {}", attachment.getFileName());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Failed to download attachment with ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/presigned-url")
    public ResponseEntity<String> getPresignedUrl(@PathVariable Long id, 
                                                  @RequestParam(defaultValue = "60") int expirationMinutes) {
        log.info("Generating presigned URL for attachment with ID: {}", id);
        
        TicketAttachmentDTO attachment = ticketAttachmentService.getAttachmentById(id);
        if (attachment == null || attachment.getFileUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String presignedUrl = s3Service.generatePresignedUrl(attachment.getFileUrl(), expirationMinutes);
            log.info("Presigned URL generated successfully for attachment ID: {}", id);
            return ResponseEntity.ok(presignedUrl);
            
        } catch (Exception e) {
            log.error("Failed to generate presigned URL for attachment with ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}