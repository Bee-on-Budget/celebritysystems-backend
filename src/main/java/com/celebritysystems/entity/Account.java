package com.celebritysystems.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@ToString(exclude = {"company", "accountPermissions", "permissions"})
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<AccountPermission> accountPermissions = new HashSet<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<Permission> permissions = new HashSet<>();
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id != null && id.equals(account.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 