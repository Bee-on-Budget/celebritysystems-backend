package com.celebritysystems.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @ManyToOne()
    @JoinColumn(name = "main_company_id", nullable = false)
    private Company mainCompany;

    @ManyToOne()
    @JoinColumn(name = "controller_company_id", nullable = false)
    private Company controllerCompany;

    @ManyToOne()
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @CreationTimestamp
    private LocalDate createdAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDate expiredAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubContract that = (SubContract) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
