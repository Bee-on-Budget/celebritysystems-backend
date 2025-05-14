package com.celebritysystems.dto;

import lombok.*;

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
}
