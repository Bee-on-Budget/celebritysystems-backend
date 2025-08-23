package com.celebritysystems.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private Long id;
    private String title;
    private String description;
    private Long createdBy;
    private Long assignedToWorkerId;
    private Long assignedBySupervisorId;
    private Long screenId;
    private String status;
    private LocalDateTime createdAt;
    private Long companyId;
    private String attachmentFileName; 
    private String serviceType;
}
