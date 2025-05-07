package com.celebritysystems.controller;

import com.celebritysystems.entity.AccountPermission;
import com.celebritysystems.service.AccountPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-permissions")
@RequiredArgsConstructor
public class AccountPermissionController {

    private final AccountPermissionService accountPermissionService;

    @GetMapping
    public ResponseEntity<List<AccountPermission>> getAllAccountPermissions() {
        return ResponseEntity.ok(accountPermissionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountPermission> getAccountPermissionById(@PathVariable Long id) {
        return accountPermissionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AccountPermission> createAccountPermission(@RequestBody AccountPermission accountPermission) {
        return ResponseEntity.ok(accountPermissionService.save(accountPermission));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountPermission> updateAccountPermission(@PathVariable Long id, @RequestBody AccountPermission accountPermission) {
        return accountPermissionService.findById(id)
                .map(existingAccountPermission -> {
                    accountPermission.setId(id);
                    return ResponseEntity.ok(accountPermissionService.save(accountPermission));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountPermission(@PathVariable Long id) {
        return accountPermissionService.findById(id)
                .map(accountPermission -> {
                    accountPermissionService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 