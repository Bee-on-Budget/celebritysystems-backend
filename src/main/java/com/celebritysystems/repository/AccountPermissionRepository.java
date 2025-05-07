package com.celebritysystems.repository;

import com.celebritysystems.entity.AccountPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountPermissionRepository extends JpaRepository<AccountPermission, Long> {
    List<AccountPermission> findByAccountId(Long accountId);
    List<AccountPermission> findByPermissionId(Long permissionId);
} 