package com.celebritysystems.service;

import com.celebritysystems.entity.AccountPermission;
import java.util.List;
import java.util.Optional;

public interface AccountPermissionService {
    List<AccountPermission> findAll();
    Optional<AccountPermission> findById(Long id);
    AccountPermission save(AccountPermission accountPermission);
    void deleteById(Long id);
} 