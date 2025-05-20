package com.celebritysystems.controller;

import com.celebritysystems.dto.ModuleDto;
import com.celebritysystems.entity.Module;
import com.celebritysystems.repository.ModuleRepository;
import com.celebritysystems.service.impl.ModuleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/module")
public class ModuleController {

    @Autowired
    private ModuleServiceImpl moduleServiceImpl;

    @Autowired
    private ModuleRepository moduleRepository;

    @PostMapping
    public ResponseEntity<?> createModule(@RequestBody ModuleDto moduleRequest) {
        try {
            Module module = moduleServiceImpl.createModule(moduleRequest).orElseThrow(() -> new RuntimeException("Failed to create company"));
            return ResponseEntity.ok(module);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
