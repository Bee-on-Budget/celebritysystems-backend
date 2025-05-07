package com.celebritysystems.repository;

import com.celebritysystems.entity.AccountPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountPermissionRepository extends JpaRepository<AccountPermission, Long> {
    List<AccountPermission> findByAccount_Id(Long accountId);
    
    List<AccountPermission> findByCompany_Id(Long companyId);
    
    List<AccountPermission> findByPermission(String permission);
    
    List<AccountPermission> findByAccount_IdAndPermission(Long accountId, String permission);
}