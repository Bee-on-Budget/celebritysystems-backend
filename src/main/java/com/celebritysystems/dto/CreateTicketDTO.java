package com.celebritysystems.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketDTO {
    private String title;
    private String description;
    private Long createdBy;
    private Long assignedToWorkerId;
    private Long assignedBySupervisorId;
    private Long screenId;
    private String status;
    private Long companyId;
    private MultipartFile file; // Optional, handled separately in TicketAttachment
}
