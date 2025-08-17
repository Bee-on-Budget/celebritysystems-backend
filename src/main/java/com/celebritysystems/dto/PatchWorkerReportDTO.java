package com.celebritysystems.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatchWorkerReportDTO {
    
    @Size(max = 2000, message = "Defects found description cannot exceed 2000 characters")
    private String defectsFound;
    
    @Size(max = 2000, message = "Solutions provided description cannot exceed 2000 characters")
    private String solutionsProvided;
}