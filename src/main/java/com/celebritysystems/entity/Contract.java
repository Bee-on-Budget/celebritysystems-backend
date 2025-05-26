package com.celebritysystems.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.celebritysystems.entity.enums.ContractType;
import com.celebritysystems.entity.enums.OperatorType;
import com.celebritysystems.entity.enums.SupplyType;

import java.time.LocalDateTime;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "contract")
public class Contract {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "info", nullable = false)
    private String info;
    
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startContractAt;
    
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;
    
    @Column(name = "company_id")
    private Long companyId;
    
    @Column(name = "screen_id")
    private Long screenId;

    @Enumerated(EnumType.STRING)
    @Column(name = "supply_type", nullable = false)
    private SupplyType supplyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator_type", nullable = false)
    private OperatorType operatorType;

    @Column(name = "account_name")
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_type", nullable = false)
    private ContractType durationType;

    @Column(name = "contract_value")
    private Double contractValue;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return id != null && id.equals(contract.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
