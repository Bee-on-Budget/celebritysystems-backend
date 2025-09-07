package com.celebritysystems.controller;

import com.celebritysystems.dto.OneSignalResponseDTO;
import com.celebritysystems.service.OneSignalService;
import com.celebritysystems.service.EmailService;
import com.celebritysystems.service.impl.OneSignalServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final OneSignalService oneSignalService;
    private final OneSignalServiceImpl oneSignalServiceImpl;
    private final EmailService emailService;

    // Configurable test email from environment
    @Value("${app.test.email:test@celebritysystems.com}")
    private String testEmail;
    
    @GetMapping("/all-players")
    public ResponseEntity<Map<String, Object>> debugAllPlayers() {
        try {
            oneSignalServiceImpl.debugAllPlayers();
            
            Map<String, Object> response = createSuccessResponse(
                "All players debug info logged. Check server logs for details.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error debugging all players", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to debug players: " + e.getMessage()));
        }
    }
    
    @PostMapping("/send-to-all")
    public ResponseEntity<Map<String, Object>> sendToAll(@RequestBody @Valid NotificationRequest request) {
        try {
            String cleanTitle = cleanText(request.getTitle());
            String cleanMessage = cleanText(request.getMessage());
            
            OneSignalResponseDTO response = oneSignalService.sendToAll(cleanTitle, cleanMessage);
            
            Map<String, Object> result = createSuccessResponse("Notification sent to all users successfully");
            result.put("oneSignalResponse", response);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error sending notification to all users", e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Failed to send notification: " + e.getMessage()));
        }
    }
    
    @PostMapping("/send-to-user")
    public ResponseEntity<Map<String, Object>> sendToUser(@RequestBody @Valid UserNotificationRequest request) {
        try {
            String cleanTitle = cleanText(request.getTitle());
            String cleanMessage = cleanText(request.getMessage());
            String cleanPlayerId = cleanText(request.getPlayerId());
            
            OneSignalResponseDTO response = oneSignalService.sendToUser(cleanTitle, cleanMessage, cleanPlayerId);
            
            Map<String, Object> result = createSuccessResponse(
                "Notification sent to user " + cleanPlayerId + " successfully");
            result.put("oneSignalResponse", response);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error sending notification to user: {}", request.getPlayerId(), e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Failed to send notification: " + e.getMessage()));
        }
    }
    
    @PostMapping("/send-to-users")
    public ResponseEntity<Map<String, Object>> sendToUsers(@RequestBody @Valid MultiUserRequest request) {
        try {
            String cleanTitle = cleanText(request.getTitle());
            String cleanMessage = cleanText(request.getMessage());
            
            OneSignalResponseDTO response = oneSignalService.sendToUsers(
                cleanTitle, cleanMessage, request.getPlayerIds());
            
            Map<String, Object> result = createSuccessResponse(
                "Notification sent to " + request.getPlayerIds().size() + " users successfully");
            result.put("oneSignalResponse", response);
            result.put("recipientCount", request.getPlayerIds().size());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error sending notification to {} users", 
                     request.getPlayerIds() != null ? request.getPlayerIds().size() : 0, e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Failed to send notification: " + e.getMessage()));
        }
    }
    
    @PostMapping("/send-with-data")
    public ResponseEntity<Map<String, Object>> sendWithData(@RequestBody @Valid DataNotificationRequest request) {
        try {
            String cleanTitle = cleanText(request.getTitle());
            String cleanMessage = cleanText(request.getMessage());
            
            OneSignalResponseDTO response = oneSignalService.sendWithData(
                cleanTitle, cleanMessage, request.getData(), request.getPlayerIds());
            
            Map<String, Object> result = createSuccessResponse(
                "Data notification sent to " + request.getPlayerIds().size() + " users successfully");
            result.put("oneSignalResponse", response);
            result.put("recipientCount", request.getPlayerIds().size());
            result.put("dataKeys", request.getData() != null ? request.getData().keySet() : null);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error sending notification with data to {} users", 
                     request.getPlayerIds() != null ? request.getPlayerIds().size() : 0, e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Failed to send notification: " + e.getMessage()));
        }
    }
    
    // EMAIL ENDPOINTS
    
    @PostMapping("/send-email")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody @Valid EmailRequest request) {
        try {
            String cleanSubject = cleanText(request.getSubject());
            String cleanMessage = cleanText(request.getMessage());
            
            emailService.sendSimpleEmail(request.getToEmail(), cleanSubject, cleanMessage);
            
            Map<String, Object> response = createSuccessResponse(
                "Email sent successfully to " + request.getToEmail());
            response.put("recipient", request.getToEmail());
            response.put("subject", cleanSubject);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending email to: {}", request.getToEmail(), e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Failed to send email: " + e.getMessage()));
        }
    }
    
    @PostMapping("/test-email-config")
    public ResponseEntity<Map<String, Object>> testEmailConfiguration() {
        try {
            String subject = "Email Configuration Test - " + LocalDateTime.now();
            String message = "This is a test email to verify that the email configuration is working correctly.\n\n" +
                           "Timestamp: " + LocalDateTime.now() + "\n" +
                           "Server: Celebrity Systems";
            
            emailService.sendSimpleEmail(testEmail, subject, message);
            
            Map<String, Object> response = createSuccessResponse(
                "Test email sent successfully to " + testEmail);
            response.put("testEmail", testEmail);
            response.put("testSubject", subject);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error testing email configuration to: {}", testEmail, e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Email configuration test failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/send-ticket-assignment-email")
    public ResponseEntity<Map<String, Object>> sendTicketAssignmentEmail(
            @RequestBody @Valid TicketAssignmentEmailRequest request) {
        try {
            emailService.sendTicketAssignmentEmail(
                request.getToEmail(),
                request.getWorkerName(),
                request.getTicketTitle(),
                request.getTicketDescription(),
                request.getTicketData()
            );
            
            Map<String, Object> response = createSuccessResponse(
                "Ticket assignment email sent successfully to " + request.getToEmail());
            response.put("recipient", request.getToEmail());
            response.put("workerName", request.getWorkerName());
            response.put("ticketTitle", request.getTicketTitle());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending ticket assignment email to: {}", request.getToEmail(), e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Failed to send ticket assignment email: " + e.getMessage()));
        }
    }
    
    @PostMapping("/send-ticket-status-update-email")
    public ResponseEntity<Map<String, Object>> sendTicketStatusUpdateEmail(
            @RequestBody @Valid TicketStatusUpdateEmailRequest request) {
        try {
            emailService.sendTicketStatusUpdateEmail(
                request.getToEmails(),
                request.getTicketTitle(),
                request.getPreviousStatus(),
                request.getNewStatus(),
                request.getTicketData()
            );
            
            Map<String, Object> response = createSuccessResponse(
                "Ticket status update email sent successfully to " + request.getToEmails().size() + " recipients");
            response.put("recipients", request.getToEmails());
            response.put("ticketTitle", request.getTicketTitle());
            response.put("statusChange", request.getPreviousStatus() + " → " + request.getNewStatus());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending ticket status update email to {} recipients", 
                     request.getToEmails() != null ? request.getToEmails().size() : 0, e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Failed to send ticket status update email: " + e.getMessage()));
        }
    }
    
    // UTILITY METHODS
    
    private String cleanText(String text) {
        if (text == null) {
            return null;
        }
        
        return text.replaceAll("[\\x00-\\x1F\\x7F]", " ")
                  .replaceAll("\\s+", " ")
                  .trim();
    }
    
    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", LocalDateTime.now());
        return error;
    }
    
    // EXCEPTION HANDLERS
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException e) {
        log.error("JSON parsing error", e);
        
        Map<String, Object> error = createErrorResponse(
            "Invalid JSON format. Please check for unescaped characters like newlines, tabs, or quotes.");
        error.put("details", "Common issues: unescaped newlines (\\n), tabs (\\t), or quotes (\") in message content");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralError(Exception e) {
        log.error("Unexpected error in NotificationController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("An unexpected error occurred"));
    }
    
    // REQUEST DTOs
    
    public static class NotificationRequest {
        @NotBlank(message = "Title cannot be blank")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        private String title;
        
        @NotBlank(message = "Message cannot be blank")
        @Size(max = 1000, message = "Message must not exceed 1000 characters")
        private String message;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    public static class UserNotificationRequest extends NotificationRequest {
        @NotBlank(message = "Player ID cannot be blank")
        @Size(max = 50, message = "Player ID must not exceed 50 characters")
        private String playerId;
        
        public String getPlayerId() { return playerId; }
        public void setPlayerId(String playerId) { this.playerId = playerId; }
    }
    
    public static class MultiUserRequest extends NotificationRequest {
        @NotEmpty(message = "Player IDs list cannot be empty")
        @Size(max = 1000, message = "Cannot send to more than 1000 users at once")
        private List<String> playerIds;
        
        public List<String> getPlayerIds() { return playerIds; }
        public void setPlayerIds(List<String> playerIds) { this.playerIds = playerIds; }
    }
    
    public static class DataNotificationRequest extends NotificationRequest {
        private Map<String, Object> data;
        
        @NotEmpty(message = "Player IDs list cannot be empty")
        @Size(max = 1000, message = "Cannot send to more than 1000 users at once")
        private List<String> playerIds;
        
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
        public List<String> getPlayerIds() { return playerIds; }
        public void setPlayerIds(List<String> playerIds) { this.playerIds = playerIds; }
    }
    
    public static class EmailRequest {
        @NotBlank(message = "To email cannot be blank")
        @Email(message = "Invalid email format")
        private String toEmail;
        
        @NotBlank(message = "Subject cannot be blank")
        @Size(max = 200, message = "Subject must not exceed 200 characters")
        private String subject;
        
        @NotBlank(message = "Message cannot be blank")
        @Size(max = 5000, message = "Message must not exceed 5000 characters")
        private String message;
        
        public String getToEmail() { return toEmail; }
        public void setToEmail(String toEmail) { this.toEmail = toEmail; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    public static class TicketAssignmentEmailRequest {
        @NotBlank(message = "To email cannot be blank")
        @Email(message = "Invalid email format")
        private String toEmail;
        
        @NotBlank(message = "Worker name cannot be blank")
        @Size(max = 100, message = "Worker name must not exceed 100 characters")
        private String workerName;
        
        @NotBlank(message = "Ticket title cannot be blank")
        @Size(max = 200, message = "Ticket title must not exceed 200 characters")
        private String ticketTitle;
        
        @NotBlank(message = "Ticket description cannot be blank")
        @Size(max = 2000, message = "Ticket description must not exceed 2000 characters")
        private String ticketDescription;
        
        private Map<String, Object> ticketData;
        
        // Getters and setters
        public String getToEmail() { return toEmail; }
        public void setToEmail(String toEmail) { this.toEmail = toEmail; }
        public String getWorkerName() { return workerName; }
        public void setWorkerName(String workerName) { this.workerName = workerName; }
        public String getTicketTitle() { return ticketTitle; }
        public void setTicketTitle(String ticketTitle) { this.ticketTitle = ticketTitle; }
        public String getTicketDescription() { return ticketDescription; }
        public void setTicketDescription(String ticketDescription) { this.ticketDescription = ticketDescription; }
        public Map<String, Object> getTicketData() { return ticketData; }
        public void setTicketData(Map<String, Object> ticketData) { this.ticketData = ticketData; }
    }
    
    public static class TicketStatusUpdateEmailRequest {
        @NotEmpty(message = "To emails list cannot be empty")
        @Size(max = 50, message = "Cannot send to more than 50 recipients at once")
        private List<@Email(message = "Invalid email format") String> toEmails;
        
        @NotBlank(message = "Ticket title cannot be blank")
        @Size(max = 200, message = "Ticket title must not exceed 200 characters")
        private String ticketTitle;
        
        @NotBlank(message = "Previous status cannot be blank")
        @Size(max = 50, message = "Previous status must not exceed 50 characters")
        private String previousStatus;
        
        @NotBlank(message = "New status cannot be blank")
        @Size(max = 50, message = "New status must not exceed 50 characters")
        private String newStatus;
        
        private Map<String, Object> ticketData;
        
        // Getters and setters
        public List<String> getToEmails() { return toEmails; }
        public void setToEmails(List<String> toEmails) { this.toEmails = toEmails; }
        public String getTicketTitle() { return ticketTitle; }
        public void setTicketTitle(String ticketTitle) { this.ticketTitle = ticketTitle; }
        public String getPreviousStatus() { return previousStatus; }
        public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }
        public String getNewStatus() { return newStatus; }
        public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
        public Map<String, Object> getTicketData() { return ticketData; }
        public void setTicketData(Map<String, Object> ticketData) { this.ticketData = ticketData; }
    }
}