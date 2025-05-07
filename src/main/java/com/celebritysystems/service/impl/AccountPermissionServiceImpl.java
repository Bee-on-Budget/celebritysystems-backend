package com.celebritysystems.service.impl;

import com.celebritysystems.entity.AccountPermission;
import com.celebritysystems.repository.AccountPermissionRepository;
import com.celebritysystems.service.AccountPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountPermissionServiceImpl implements AccountPermissionService {

    private final AccountPermissionRepository accountPermissionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AccountPermission> findAll() {
        return accountPermissionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccountPermission> findById(Long id) {
        return accountPermissionRepository.findById(id);
    }

    @Override
    public AccountPermission save(AccountPermission accountPermission) {
        return accountPermissionRepository.save(accountPermission);
    }

    @Override
    public void deleteById(Long id) {
        accountPermissionRepository.deleteById(id);
    }
} 