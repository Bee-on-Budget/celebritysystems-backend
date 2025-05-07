package com.celebritysystems.service;

import com.celebritysystems.entity.Account;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> findAll();
    Optional<Account> findById(Long id);
    Account save(Account account);
    void deleteById(Long id);
} 