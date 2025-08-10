package com.celebritysystems.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class TicketResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Long createdBy;

    private String assignedToWorkerName;      // previously assignedToWorkerId
    private String assignedBySupervisorName;  // previously assignedBySupervisorId

    private String screenName;                // previously screenId
    private String companyName;               // previously companyId

    private String status;
    private LocalDateTime createdAt;
    private String attachmentFileName;

    private String screenType;
    private String location;
    private WorkerReportResponseDTO workerReport;

    // ✅ Timestamps for each status
    private LocalDateTime openedAt;
    private LocalDateTime inProgressAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    private String ticketImageUrl;
    private String ticketImageName;
}
