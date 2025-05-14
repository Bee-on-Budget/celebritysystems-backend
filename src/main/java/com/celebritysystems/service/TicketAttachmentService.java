package com.celebritysystems.service;

import com.celebritysystems.dto.TicketAttachmentDTO;
import com.celebritysystems.dto.CreateTicketAttachmentDTO;


public interface TicketAttachmentService {
    TicketAttachmentDTO getAttachmentById(Long id);
    TicketAttachmentDTO addAttachment(CreateTicketAttachmentDTO attachmentDTO);
    void deleteAttachment(Long id);
}

