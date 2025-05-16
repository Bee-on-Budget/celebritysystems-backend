package com.celebritysystems.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketAttachmentDTO {
    private Long ticketId;
    private MultipartFile filePath;
    private String note;
    private Long uploadedBy;
}
