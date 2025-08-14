package com.celebritysystems.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerReportDTO {
    
    @NotNull
    @Valid
    private ReportData report;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportData {
        private String date;
        
        @JsonProperty("service_type")
        private String serviceType;
        
        @Valid
        private ChecklistData checklist;
        
        @JsonProperty("date_time")
        private LocalDateTime dateTime;
        
        @JsonProperty("defects_found")
        private String defectsFound;
        
        @JsonProperty("solutions_provided")
        private String solutionsProvided;
        
        @JsonProperty("service_supervisor_signatures")
        private MultipartFile serviceSupervisorSignatures;
        
        @JsonProperty("technician_signatures")
        private MultipartFile technicianSignatures;
        
        @JsonProperty("authorized_person_Signatures")
        private MultipartFile authorizedPersonSignatures;
        
        @JsonProperty("solution_image")
        private MultipartFile solutionImage;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChecklistData {
        @JsonProperty("Data Cables (Cat6/RJ45)")
        private String dataCables;
        
        @JsonProperty("Power Cable")
        private String powerCable;
        
        @JsonProperty("Power Supplies")
        private String powerSupplies;
        
        @JsonProperty("LED Modules")
        private String ledModules;
        
        @JsonProperty("Cooling Systems")
        private String coolingSystems;
        
        @JsonProperty("Service Lights & Sockets")
        private String serviceLights;
        
        @JsonProperty("Operating Computers")
        private String operatingComputers;
        
        @JsonProperty("Software")
        private String software;
        
        @JsonProperty("Power DBs")
        private String powerDBs;
        
        @JsonProperty("Media Converters")
        private String mediaConverters;
        
        @JsonProperty("Control Systems")
        private String controlSystems;
        
        @JsonProperty("Video Processors")
        private String videoProcessors;
    }
}
