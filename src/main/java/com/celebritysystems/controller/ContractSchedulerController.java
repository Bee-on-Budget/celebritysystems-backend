package com.celebritysystems.controller;

import com.celebritysystems.scheduler.ContractExpiryNotificationScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/scheduler")
@RequiredArgsConstructor
public class ContractSchedulerController {

    private final ContractExpiryNotificationScheduler contractExpiryScheduler;


    @PostMapping("/trigger-contract-expiry-check")
    public ResponseEntity<?> triggerContractExpiryCheck() {
        log.info("Manual contract expiry check triggered via API");
        
        try {
            contractExpiryScheduler.triggerManualCheck();
            return ResponseEntity.ok(Map.of(
                "message", "Contract expiry check triggered successfully",
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("Error triggering manual contract expiry check", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to trigger contract expiry check",
                "message", e.getMessage()
            ));
        }
    }
}
