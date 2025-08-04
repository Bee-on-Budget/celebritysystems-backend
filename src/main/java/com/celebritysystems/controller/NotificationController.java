package com.celebritysystems.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import com.celebritysystems.dto.OneSignalResponseDTO;
import com.celebritysystems.service.OneSignalService;
import com.celebritysystems.service.impl.OneSignalServiceImpl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
        private final OneSignalServiceImpl oneSignalServiceImpl; // Add this injection

    private final OneSignalService oneSignalService;
    private final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    public NotificationController(OneSignalService oneSignalService, OneSignalServiceImpl oneSignalServiceImpl) {
        this.oneSignalService = oneSignalService;
                this.oneSignalServiceImpl = oneSignalServiceImpl;

    }
   @GetMapping("/all-players")
    public ResponseEntity<Map<String, Object>> debugAllPlayers() {
        oneSignalServiceImpl.debugAllPlayers();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "All players debug info logged. Check server logs for details.");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/send-to-all")
    public ResponseEntity<?> sendToAll(@RequestBody @Valid NotificationRequest request) {
        try {
            // Clean the message content
            String cleanTitle = cleanText(request.getTitle());
            String cleanMessage = cleanText(request.getMessage());
            
            OneSignalResponseDTO response = oneSignalService.sendToAll(cleanTitle, cleanMessage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error sending notification to all users", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to send notification: " + e.getMessage()));
        }
    }
    
    @PostMapping("/send-to-user")
    public ResponseEntity<?> sendToUser(@RequestBody @Valid UserNotificationRequest request) {
        try {
            String cleanTitle = cleanText(request.getTitle());
            String cleanMessage = cleanText(request.getMessage());
            String cleanPlayerId = cleanText(request.getPlayerId());
            
            OneSignalResponseDTO response = oneSignalService.sendToUser(cleanTitle, cleanMessage, cleanPlayerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error sending notification to user", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to send notification: " + e.getMessage()));
        }
    }
    
    @PostMapping("/send-to-users")
    public ResponseEntity<?> sendToUsers(@RequestBody @Valid MultiUserRequest request) {
        try {
            String cleanTitle = cleanText(request.getTitle());
            String cleanMessage = cleanText(request.getMessage());
            
            OneSignalResponseDTO response = oneSignalService.sendToUsers(cleanTitle, cleanMessage, request.getPlayerIds());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error sending notification to users", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to send notification: " + e.getMessage()));
        }
    }
    
    @PostMapping("/send-with-data")
    public ResponseEntity<?> sendWithData(@RequestBody @Valid DataNotificationRequest request) {
        try {
            String cleanTitle = cleanText(request.getTitle());
            String cleanMessage = cleanText(request.getMessage());
            
            OneSignalResponseDTO response = oneSignalService.sendWithData(
                cleanTitle, cleanMessage, request.getData(), request.getPlayerIds());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error sending notification with data", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to send notification: " + e.getMessage()));
        }
    }
    
    /**
     * Clean text by removing or escaping control characters
     */
    private String cleanText(String text) {
        if (text == null) {
            return null;
        }
        
        // Remove or replace control characters
        return text.replaceAll("[\\x00-\\x1F\\x7F]", " ") // Replace control chars with space
                  .replaceAll("\\s+", " ") // Replace multiple spaces with single space
                  .trim();
    }
    
    /**
     * Create standardized error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
    
    /**
     * Handle JSON parsing errors
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException e) {
        logger.error("JSON parsing error", e);
        
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", "Invalid JSON format. Please check for unescaped characters like newlines, tabs, or quotes.");
        error.put("details", "Common issues: unescaped newlines (\\n), tabs (\\t), or quotes (\") in message content");
        error.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralError(Exception e) {
        logger.error("Unexpected error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("An unexpected error occurred"));
    }
    
    // Request DTOs with validation
    public static class NotificationRequest {
        @NotBlank(message = "Title cannot be blank")
        private String title;
        
        @NotBlank(message = "Message cannot be blank")
        private String message;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    public static class UserNotificationRequest extends NotificationRequest {
        @NotBlank(message = "Player ID cannot be blank")
        private String playerId;
        
        public String getPlayerId() { return playerId; }
        public void setPlayerId(String playerId) { this.playerId = playerId; }
    }
    
    public static class MultiUserRequest extends NotificationRequest {
        @NotEmpty(message = "Player IDs list cannot be empty")
        private List<String> playerIds;
        
        public List<String> getPlayerIds() { return playerIds; }
        public void setPlayerIds(List<String> playerIds) { this.playerIds = playerIds; }
    }
    
    public static class DataNotificationRequest extends NotificationRequest {
        private Map<String, Object> data;
        
        @NotEmpty(message = "Player IDs list cannot be empty")
        private List<String> playerIds;
        
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
        public List<String> getPlayerIds() { return playerIds; }
        public void setPlayerIds(List<String> playerIds) { this.playerIds = playerIds; }
    }
}