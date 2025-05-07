package com.celebritysystems.entity;

import jakarta.persistence.*;
import lombok.*;
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
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;
    
    @Column(name = "company_id")
    private Long companyId;
    
    @Column(name = "screen_id")
    private Long screenId;
    
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