package com.celebritysystems.controller;

import com.celebritysystems.dto.CabinDto;
import com.celebritysystems.entity.Cabin;
import com.celebritysystems.repository.CabinRepository;
import com.celebritysystems.service.CabinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cabin")
@RequiredArgsConstructor
public class CabinController {
    private final CabinService cabinService;

    private final CabinRepository cabinRepository;

    @PostMapping
    public ResponseEntity<?> createCabin(@RequestBody CabinDto cabinRequest) {
        try {
            Cabin cabin = cabinService.createCabin(cabinRequest)
                    .orElseThrow(() -> new RuntimeException("Failed to create company"));
            return ResponseEntity.ok(cabin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
