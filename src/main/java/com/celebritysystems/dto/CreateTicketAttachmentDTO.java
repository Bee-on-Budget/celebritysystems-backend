package com.celebritysystems.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketAttachmentDTO {
    private Long ticketId;
    private String filePath;
    private String note;
    private Long uploadedBy;
}
