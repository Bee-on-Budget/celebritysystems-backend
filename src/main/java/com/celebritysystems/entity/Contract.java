package com.celebritysystems.entity;

import com.celebritysystems.entity.enums.ContractType;
import com.celebritysystems.entity.enums.OperatorType;
import com.celebritysystems.entity.enums.SupplyType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "contract_screens", joinColumns = @JoinColumn(name = "contract_id"))
    @Column(name = "screen_id")
    private List<Long> screenIds;

    @ElementCollection
    @CollectionTable(name = "contract_account_permissions", joinColumns = @JoinColumn(name = "contract_id"))
    private List<AccountPermission> accountPermissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contract)) return false;
        Contract contract = (Contract) o;
        return id != null && id.equals(contract.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
