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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketAttachmentServiceImpl implements TicketAttachmentService {

    private final TicketAttachmentRepository ticketAttachmentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

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

        byte[] file = toBytes(dto.getFilePath());

        TicketAttachment attachment = TicketAttachment.builder()
                .ticket(ticket)
                .fileData(file)
                .note(dto.getNote())
                .uploadedBy(uploadedBy)
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
                .ticketId(attachment.getTicketId())
                .note(attachment.getNote())
                .uploadedBy(attachment.getUploadedBy() != null ? attachment.getUploadedBy().getId() : null)
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
