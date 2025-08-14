package com.celebritysystems.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerReportResponseDTO {
    private Long id;
    private Long ticketId;
    private LocalDateTime reportDate;
    private String serviceType;
    
    private Map<String, String> checklist;
    
    private LocalDateTime dateTime;
    private String defectsFound;
    private String solutionsProvided;
    private String serviceSupervisorSignatures;
    private String technicianSignatures;
    private String authorizedPersonSignatures;
    private String solutionImage;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
