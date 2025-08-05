package com.celebritysystems.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAttachmentDTO {
    private Long id;
    private Long ticketId;
    private String fileUrl;
    private String fileName;
    private String note;
    private Long uploadedBy;
    private LocalDateTime uploadedAt;
}
