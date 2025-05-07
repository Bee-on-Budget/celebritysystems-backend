package com.celebritysystems.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString(exclude = {"companyMain", "companyAccess"})
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "company_relations")
public class CompanyRelations {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_main_id")
    private Company companyMain;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_access_id")
    private Company companyAccess;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyRelations that = (CompanyRelations) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 