package com.celebritysystems.controller;

import com.celebritysystems.entity.Cabin;
import com.celebritysystems.entity.Module;
import com.celebritysystems.repository.CabinRepository;
import com.celebritysystems.repository.ModuleRepository;
import com.celebritysystems.service.impl.CabinServiceImpl;
import com.celebritysystems.service.impl.ModuleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cabin")
public class CabinController {
    @Autowired
    private CabinServiceImpl cabinServiceImpl;

    @Autowired
    private CabinRepository cabinRepository;

    @PostMapping
    public ResponseEntity<?> createCabin(@RequestBody Cabin cabinRequest) {
        try {
            Cabin cabin = cabinServiceImpl.createCabin(cabinRequest);
//                    .orElseThrow(() -> new RuntimeException("Failed to create company"));
            return ResponseEntity.ok(cabin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
