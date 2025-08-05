package com.celebritysystems.service.impl;

import com.celebritysystems.dto.TicketAttachmentDTO;
import com.celebritysystems.dto.CreateTicketAttachmentDTO;
import com.celebritysystems.entity.Ticket;
import com.celebritysystems.entity.TicketAttachment;
import com.celebritysystems.entity.User;
import com.celebritysystems.repository.TicketAttachmentRepository;
import com.celebritysystems.repository.TicketRepository;
import com.celebritysystems.repository.UserRepository;
import com.celebritysystems.service.TicketAttachmentService;
import com.celebritysystems.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketAttachmentServiceImpl implements TicketAttachmentService {

    private final TicketAttachmentRepository ticketAttachmentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Override
    public TicketAttachmentDTO getAttachmentById(Long id) {
        return ticketAttachmentRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    @Override
    public TicketAttachmentDTO addAttachment(CreateTicketAttachmentDTO dto) {
        Ticket ticket = ticketRepository.findById(dto.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User uploadedBy = userRepository.findById(dto.getUploadedBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fileUrl = s3Service.uploadFile(dto.getFilePath(), "ticket-attachments");

        TicketAttachment attachment = TicketAttachment.builder()
                .ticket(ticket)
                .fileUrl(fileUrl)
                .fileName(dto.getFilePath().getOriginalFilename())
                .note(dto.getNote())
                .uploadedBy(uploadedBy)
                .build();

        return toDTO(ticketAttachmentRepository.save(attachment));
    }

    @Override
    public void deleteAttachment(Long id) {
        TicketAttachment attachment = ticketAttachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        
        if (attachment.getFileUrl() != null) {
            s3Service.deleteFile(attachment.getFileUrl());
        }
        
        ticketAttachmentRepository.deleteById(id);
    }

    private TicketAttachmentDTO toDTO(TicketAttachment attachment) {
        return TicketAttachmentDTO.builder()
                .id(attachment.getId())
                .ticketId(attachment.getTicketId())
                .fileUrl(attachment.getFileUrl())
                .fileName(attachment.getFileName())
                .note(attachment.getNote())
                .uploadedBy(attachment.getUploadedBy() != null ? attachment.getUploadedBy().getId() : null)
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }

}
