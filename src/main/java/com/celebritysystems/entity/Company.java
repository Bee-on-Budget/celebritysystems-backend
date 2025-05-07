package com.celebritysystems.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@ToString(exclude = {"accounts", "contracts", "mainRelations", "accessRelations"})
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "company", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "phone", nullable = false)
    private String phone;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "location", nullable = false)
    private String location;
    
    @Column(name = "company_type", nullable = false)
    private String companyType;
    
    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<Account> accounts = new HashSet<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<Contract> contracts = new HashSet<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "companyMain", cascade = CascadeType.ALL)
    private Set<CompanyRelations> mainRelations = new HashSet<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "companyAccess", cascade = CascadeType.ALL)
    private Set<CompanyRelations> accessRelations = new HashSet<>();
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return id != null && id.equals(company.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 