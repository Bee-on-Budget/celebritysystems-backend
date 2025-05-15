package com.celebritysystems.controller;

import com.celebritysystems.dto.CreateScreenRequestDto;
import com.celebritysystems.service.impl.ScreenServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;

@RestController
@RequestMapping("/api/screens")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenServiceImpl screenServiceImpl;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createScreen(@ModelAttribute CreateScreenRequestDto request) {
        try {
            screenServiceImpl.createScreen(request);
            return ResponseEntity.ok("Screen created successfully");
        } catch (MultipartException e) {
            return ResponseEntity.badRequest().body("Invalid file upload");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating screen: " + e.getMessage());
        }
    }
}