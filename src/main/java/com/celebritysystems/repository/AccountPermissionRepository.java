package com.celebritysystems.repository;

import com.celebritysystems.entity.AccountPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountPermissionRepository extends JpaRepository<AccountPermission, Long> {
    List<AccountPermission> findByAccountId(Long accountId);
    
    List<AccountPermission> findByCompanyId(Long companyId);
    
    List<AccountPermission> findByPermission(String permission);
    
    List<AccountPermission> findByAccountIdAndPermission(Long accountId, String permission);
}