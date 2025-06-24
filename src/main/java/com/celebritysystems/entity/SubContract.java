package com.celebritysystems.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "sub_contract")
public class SubContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_company_id", nullable = false)
    private Company mainCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controller_company_id", nullable = false)
    private Company controllerCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDate expiredAt;
}
