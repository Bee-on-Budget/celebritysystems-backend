package com.celebritysystems.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "worker_report")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkerReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", unique = true)
    private Ticket ticket;

    private LocalDateTime reportDate;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    // Checklist fields
    private String dataCables;
    private String powerCable;
    private String powerSupplies;
    private String ledModules;
    private String coolingSystems;
    private String serviceLights;
    private String operatingComputers;
    private String software;
    private String powerDBs;
    private String mediaConverters;
    private String controlSystems;
    private String videoProcessors;

    private LocalDateTime dateTime;

    @Column(columnDefinition = "TEXT")
    private String defectsFound;

    @Column(columnDefinition = "TEXT")
    private String solutionsProvided;

    private String serviceSupervisorSignatures;
    private String technicianSignatures;
    private String authorizedPersonSignatures;
    private String solutionImage;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ServiceType {
        REGULAR_SERVICE("Regular Service"),
        EMERGENCY_SERVICE("Emergency Service"),
        PREVENTIVE_MAINTENANCE("Preventive Maintenance");

        private final String displayName;

        ServiceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}