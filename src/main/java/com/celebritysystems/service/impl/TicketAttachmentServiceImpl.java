package com.celebritysystems.service.impl;

import com.celebritysystems.dto.TicketAttachmentDTO;
import com.celebritysystems.dto.CreateTicketAttachmentDTO;
import com.celebritysystems.entity.Ticket;
import com.celebritysystems.entity.TicketAttachment;
import com.celebritysystems.repository.TicketAttachmentRepository;
import com.celebritysystems.repository.TicketRepository;
import com.celebritysystems.service.TicketAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketAttachmentServiceImpl implements TicketAttachmentService {

    private final TicketAttachmentRepository ticketAttachmentRepository;
    private final TicketRepository ticketRepository; // You'll need this



    @Override
    public TicketAttachmentDTO getAttachmentById(Long id) {
        return ticketAttachmentRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    public TicketAttachmentDTO addAttachment(CreateTicketAttachmentDTO dto) {
        Ticket ticket = ticketRepository.findById(dto.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        byte[] file = toBytes(dto.getFilePath());
        
        TicketAttachment attachment = TicketAttachment.builder()
                .ticket(ticket) // Set the Ticket object, not the ID
                .filePath(file)
                .note(dto.getNote())
                .uploadedAt(LocalDateTime.now())
                .build();
        return toDTO(ticketAttachmentRepository.save(attachment));
    }

    @Override
    public void deleteAttachment(Long id) {
        ticketAttachmentRepository.deleteById(id);
    }

    private TicketAttachmentDTO toDTO(TicketAttachment attachment) {
        return TicketAttachmentDTO.builder()
                .id(attachment.getId())
                .ticketId(attachment.getTicketId()) // This will use our new convenience method
//                .filePath(attachment.getFilePath())
                .note(attachment.getNote())
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }

    private byte[] toBytes(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + file.getOriginalFilename(), e);
        }
    }
}