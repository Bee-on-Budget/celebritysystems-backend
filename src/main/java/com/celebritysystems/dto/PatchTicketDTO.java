package com.celebritysystems.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchTicketDTO {
    private Long assignedToWorkerId;
    private Long assignedBySupervisorId;
    private String status;
    
    public boolean hasAssignedToWorkerId() {
        return assignedToWorkerId != null;
    }
    
    public boolean hasAssignedBySupervisorId() {
        return assignedBySupervisorId != null;
    }
    
    public boolean hasStatus() {
        return status != null && !status.trim().isEmpty();
    }
}