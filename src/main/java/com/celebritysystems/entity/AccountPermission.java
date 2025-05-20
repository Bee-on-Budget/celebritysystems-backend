//package com.celebritysystems.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@ToString
//@Setter
//@Getter
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "account_permission")
//public class AccountPermission {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "permission", nullable = false)
//    private String permission;
//
//    @Column(name = "account_id")
//    private Long accountId;
//
//    @Column(name = "company_id")
//    private Long companyId;
//
//    public AccountPermission(String permission) {
//        this.permission = permission;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        AccountPermission that = (AccountPermission) o;
//        return id != null && id.equals(that.id);
//    }
//
//    @Override
//    public int hashCode() {
//        return getClass().hashCode();
//    }
//}