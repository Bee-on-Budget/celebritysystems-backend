package com.celebritysystems.repository;

import com.celebritysystems.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByAccountId(Long accountId);
    List<Permission> findByPermissionType(String permissionType);
    Optional<Permission> findByName(String name);
} 