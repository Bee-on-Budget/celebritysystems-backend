package com.celebritysystems.controller;

import com.celebritysystems.dto.CabinDto;
import com.celebritysystems.entity.Cabin;
import com.celebritysystems.repository.CabinRepository;
import com.celebritysystems.service.CabinService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cabin")
@RequiredArgsConstructor
public class CabinController {

    private final CabinService cabinService;
    private final CabinRepository cabinRepository;

    @PostMapping
    public ResponseEntity<?> createCabin(@Valid @RequestBody CabinDto cabinRequest) {
        log.info("Received request to create a new cabin");
        try {
            log.debug("Cabin creation payload: {}", cabinRequest.toString());

            Cabin cabin = cabinService.createCabin(cabinRequest)
                    .orElseThrow(() -> new RuntimeException("Failed to create cabin"));
            log.info("Successfully created cabin with ID: {}", cabin.getId());

            return ResponseEntity.ok(cabin);
        } catch (IllegalArgumentException e) {
            log.error("Validation error during cabin creation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("VALIDATION_ERROR", e.getMessage())
            );
        } catch (RuntimeException e) {
            log.error("Cabin creation failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("CREATION_FAILED", e.getMessage())
            );
        } catch (Exception e) {
            log.error("Unexpected error during cabin creation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("INTERNAL_SERVER_ERROR",
                            "An unexpected error occurred: " + e.getMessage())
            );
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ErrorResponse {
        private String errorCode;
        private String message;
    }
}
