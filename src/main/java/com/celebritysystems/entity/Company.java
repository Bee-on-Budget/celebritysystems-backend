package com.celebritysystems.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@ToString
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
    
    @Column(name = "location")
    private String location;

    @Column(name = "activated", nullable = false)
    private Boolean activated;

    @Column(name = "company_type", nullable = false)
    private String companyType;
    
    @OneToMany(mappedBy = "id")
    private List<User> user_id;

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