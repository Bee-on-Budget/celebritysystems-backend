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

    @Column(nullable = true)
    private String serviceSupervisorSignatures;
    @Column(nullable = true)
    private String technicianSignatures;
    @Column(nullable = true)
    private String technicianSignaturesName;
    @Column(nullable = true)
    private String authorizedPersonSignatures;
    @Column(nullable = true)
    private String solutionImage;
    @Column(nullable = true)
    private String solutionImageName;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
