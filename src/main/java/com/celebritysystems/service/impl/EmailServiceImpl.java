package com.celebritysystems.service.impl;

import com.celebritysystems.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        log.info("=== EMAIL SEND ATTEMPT ===");
        log.info("FROM: {}", fromEmail);
        log.info("TO: {}", to);
        log.info("SUBJECT: {}", subject);
        log.info("BODY LENGTH: {} characters", text != null ? text.length() : 0);
        
        // Validate inputs
        if (to == null || to.trim().isEmpty()) {
            log.error("EMAIL FAILED: Recipient email is null or empty");
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }
        
        if (subject == null || subject.trim().isEmpty()) {
            log.warn("EMAIL WARNING: Subject is null or empty");
        }
        
        if (text == null || text.trim().isEmpty()) {
            log.warn("EMAIL WARNING: Email body is null or empty");
        }
        
        try {
            log.info("Creating SimpleMailMessage...");
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            log.info("Message created successfully. Attempting to send...");
            log.debug("Full message details: From={}, To={}, Subject={}", 
                     message.getFrom(), message.getTo(), message.getSubject());
            
            long startTime = System.currentTimeMillis();
            mailSender.send(message);
            long endTime = System.currentTimeMillis();
            
            log.info("✅ EMAIL SENT SUCCESSFULLY to: {} (took {}ms)", to, (endTime - startTime));
            log.info("Email sent at: {}", LocalDateTime.now().format(DATE_FORMATTER));
            
        } catch (org.springframework.mail.MailAuthenticationException e) {
            log.error("❌ EMAIL AUTHENTICATION FAILED: Check username/password for {}", fromEmail);
            log.error("Authentication error details: {}", e.getMessage(), e);
            throw new RuntimeException("Email authentication failed - check credentials", e);
        } catch (org.springframework.mail.MailSendException e) {
            log.error("❌ EMAIL SEND FAILED: Mail server rejected the message");
            log.error("Send error details: {}", e.getMessage(), e);
            log.error("Failed messages: {}", e.getFailedMessages());
            throw new RuntimeException("Email send failed - server rejected message", e);
        } catch (org.springframework.mail.MailException e) {
            log.error("❌ GENERAL MAIL ERROR: {}", e.getMessage(), e);
            throw new RuntimeException("Email system error", e);
        } catch (Exception e) {
            log.error("❌ UNEXPECTED EMAIL ERROR to {}: {}", to, e.getMessage(), e);
            log.error("Error class: {}", e.getClass().getSimpleName());
            throw new RuntimeException("Unexpected error sending email", e);
        }
    }
    
    @Override
    public void sendEmailWithAttachment(String to, String subject, String text, 
                                       Resource attachment, String attachmentFileName) {
        log.info("=== EMAIL WITH ATTACHMENT SEND ATTEMPT ===");
        log.info("FROM: {}", fromEmail);
        log.info("TO: {}", to);
        log.info("SUBJECT: {}", subject);
        log.info("ATTACHMENT: {}", attachmentFileName);
        
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            
            if (attachment != null && attachmentFileName != null) {
                helper.addAttachment(attachmentFileName, attachment);
                log.info("Attachment added: {}", attachmentFileName);
            }
            
            long startTime = System.currentTimeMillis();
            mailSender.send(mimeMessage);
            long endTime = System.currentTimeMillis();
            
            log.info("✅ EMAIL WITH ATTACHMENT SENT SUCCESSFULLY to: {} (took {}ms)", to, (endTime - startTime));
            
        } catch (MessagingException e) {
            log.error("❌ EMAIL MESSAGING ERROR to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Email messaging error", e);
        } catch (Exception e) {
            log.error("❌ UNEXPECTED EMAIL ERROR to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Unexpected error sending email with attachment", e);
        }
    }
    
    @Override
    public void sendEmailWithAttachment(List<String> toEmails, String subject, String text, 
                                       Resource attachment, String attachmentFileName) {
        log.info("=== BULK EMAIL WITH ATTACHMENT SEND ATTEMPT ===");
        log.info("Recipients: {}, Subject: {}, Attachment: {}", toEmails.size(), subject, attachmentFileName);
        
        if (toEmails == null || toEmails.isEmpty()) {
            log.warn("❌ No email addresses provided for bulk send with attachment");
            return;
        }
        
        int successCount = 0;
        int failureCount = 0;
        
        for (String email : toEmails) {
            try {
                sendEmailWithAttachment(email, subject, text, attachment, attachmentFileName);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to send email with attachment to {}: {}", email, e.getMessage());
                failureCount++;
            }
        }
        
        log.info("✅ Bulk emails with attachment sent: {} success, {} failures out of {} total", 
                successCount, failureCount, toEmails.size());
    }
    
    @Override
    public void sendTicketAssignmentEmail(String toEmail, String workerName, String ticketTitle, 
                                        String ticketDescription, Map<String, Object> ticketData) {
        log.info("=== TICKET ASSIGNMENT EMAIL ===");
        log.info("Worker: {}, Email: {}, Ticket: {}", workerName, toEmail, ticketTitle);
        
        try {
            String subject = "New Ticket Assigned - " + ticketTitle;
            
            String emailBody = buildTicketAssignmentText(workerName, ticketTitle, 
                                                       ticketDescription, ticketData);
            
            log.info("Built email body for ticket assignment (length: {} chars)", emailBody.length());
            log.debug("Email body preview: {}", emailBody.substring(0, Math.min(100, emailBody.length())));
            
            sendSimpleEmail(toEmail, subject, emailBody);
            log.info("✅ Ticket assignment email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("❌ Failed to send ticket assignment email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send ticket assignment email", e);
        }
    }
    
    @Override
    public void sendTicketStatusUpdateEmail(List<String> toEmails, String ticketTitle, 
                                          String previousStatus, String newStatus, 
                                          Map<String, Object> ticketData) {
        log.info("=== TICKET STATUS UPDATE EMAIL ===");
        log.info("Recipients: {}, Ticket: {}, Status: {} -> {}", 
                toEmails.size(), ticketTitle, previousStatus, newStatus);
        log.info("Email addresses: {}", toEmails);
        
        if (toEmails == null || toEmails.isEmpty()) {
            log.warn("❌ No email addresses provided for status update");
            return;
        }
        
        try {
            String subject = "Ticket Status Update - " + ticketTitle;
            
            String emailBody = buildTicketStatusUpdateText(ticketTitle, previousStatus, 
                                                         newStatus, ticketData);
            
            log.info("Built email body for status update (length: {} chars)", emailBody.length());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (String email : toEmails) {
                try {
                    log.info("Sending status update email to: {}", email);
                    sendSimpleEmail(email, subject, emailBody);
                    successCount++;
                } catch (Exception e) {
                    log.error("Failed to send status update email to {}: {}", email, e.getMessage());
                    failureCount++;
                }
            }
            
            log.info("✅ Status update emails sent: {} success, {} failures out of {} total", 
                    successCount, failureCount, toEmails.size());
            
        } catch (Exception e) {
            log.error("❌ Failed to send ticket status update emails: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send ticket status update emails", e);
        }
    }
    
    @Override
    public void sendTicketCompletionEmailWithPdf(List<String> toEmails, String ticketTitle, 
                                                Map<String, Object> ticketData, 
                                                Resource pdfAttachment, String pdfFileName) {
        log.info("=== TICKET COMPLETION EMAIL WITH PDF ===");
        log.info("Recipients: {}, Ticket: {}, PDF: {}", toEmails.size(), ticketTitle, pdfFileName);
        log.info("Email addresses: {}", toEmails);
        
        if (toEmails == null || toEmails.isEmpty()) {
            log.warn("❌ No email addresses provided for ticket completion");
            return;
        }
        
        try {
            String subject = "Ticket Completed - " + ticketTitle;
            String emailBody = buildTicketCompletionText(ticketTitle, ticketData);
            
            log.info("Built email body for ticket completion (length: {} chars)", emailBody.length());
            
            sendEmailWithAttachment(toEmails, subject, emailBody, pdfAttachment, pdfFileName);
            log.info("✅ Ticket completion emails with PDF sent successfully");
            
        } catch (Exception e) {
            log.error("❌ Failed to send ticket completion emails with PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send ticket completion emails with PDF", e);
        }
    }
    
    private String buildTicketAssignmentText(String workerName, String ticketTitle, 
                                           String ticketDescription, Map<String, Object> ticketData) {
        
        log.debug("Building ticket assignment email body...");
        
        String companyName = (String) ticketData.getOrDefault("companyName", "N/A");
        String screenName = (String) ticketData.getOrDefault("screenName", "N/A");
        String screenLocation = (String) ticketData.getOrDefault("screenLocation", "N/A");
        String createdBy = (String) ticketData.getOrDefault("createdBy", "N/A");
        String ticketId = (String) ticketData.getOrDefault("ticketId", "N/A");
        
        log.debug("Ticket data: ID={}, Company={}, Screen={}, Location={}, CreatedBy={}", 
                 ticketId, companyName, screenName, screenLocation, createdBy);
        
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("NEW TICKET ASSIGNED\n");
        emailBody.append("==================\n\n");
        emailBody.append("Hello ").append(workerName).append(",\n\n");
        emailBody.append("A new ticket has been assigned to you. Please review the details below:\n\n");
        
        emailBody.append("TICKET DETAILS:\n");
        emailBody.append("---------------\n");
        emailBody.append("Ticket ID: #").append(ticketId).append("\n");
        emailBody.append("Title: ").append(ticketTitle).append("\n");
        emailBody.append("Description: ").append(ticketDescription).append("\n");
        emailBody.append("Company: ").append(companyName).append("\n");
        emailBody.append("Screen: ").append(screenName).append("\n");
        emailBody.append("Location: ").append(screenLocation).append("\n");
        emailBody.append("Created By: ").append(createdBy).append("\n");
        emailBody.append("Assigned At: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n\n");
        
        emailBody.append("Please log into the system to view more details and begin working on this ticket.\n\n");
        emailBody.append("Thank you!\n");
        emailBody.append("Celebrity Systems Team");
        
        String result = emailBody.toString();
        log.debug("Email body built successfully (length: {} chars)", result.length());
        
        return result;
    }
    
    private String buildTicketStatusUpdateText(String ticketTitle, String previousStatus, 
                                             String newStatus, Map<String, Object> ticketData) {
        
        log.debug("Building ticket status update email body...");
        
        String companyName = (String) ticketData.getOrDefault("companyName", "N/A");
        String screenName = (String) ticketData.getOrDefault("screenName", "N/A");
        String assignedWorker = (String) ticketData.getOrDefault("assignedWorker", "N/A");
        String ticketId = (String) ticketData.getOrDefault("ticketId", "N/A");
        
        log.debug("Status update data: ID={}, Company={}, Screen={}, Worker={}", 
                 ticketId, companyName, screenName, assignedWorker);
        
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("TICKET STATUS UPDATED\n");
        emailBody.append("====================\n\n");
        emailBody.append("Hello,\n\n");
        emailBody.append("The status of one of your tickets has been updated:\n\n");
        
        emailBody.append("STATUS CHANGE:\n");
        emailBody.append("--------------\n");
        emailBody.append(previousStatus).append(" → ").append(newStatus).append("\n\n");
        
        emailBody.append("TICKET DETAILS:\n");
        emailBody.append("---------------\n");
        emailBody.append("Ticket ID: #").append(ticketId).append("\n");
        emailBody.append("Title: ").append(ticketTitle).append("\n");
        emailBody.append("Company: ").append(companyName).append("\n");
        emailBody.append("Screen: ").append(screenName).append("\n");
        emailBody.append("Assigned Worker: ").append(assignedWorker).append("\n");
        emailBody.append("Updated At: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n\n");
        
        emailBody.append("Please log into the system to view more details.\n\n");
        emailBody.append("Thank you!\n");
        emailBody.append("Celebrity Systems Team");
        
        String result = emailBody.toString();
        log.debug("Status update email body built successfully (length: {} chars)", result.length());
        
        return result;
    }
    
    private String buildTicketCompletionText(String ticketTitle, Map<String, Object> ticketData) {
        log.debug("Building ticket completion email body...");
        
        String companyName = (String) ticketData.getOrDefault("companyName", "N/A");
        String screenName = (String) ticketData.getOrDefault("screenName", "N/A");
        String screenLocation = (String) ticketData.getOrDefault("screenLocation", "N/A");
        String assignedWorker = (String) ticketData.getOrDefault("assignedWorker", "N/A");
        String ticketId = (String) ticketData.getOrDefault("ticketId", "N/A");
        String defectsFound = (String) ticketData.getOrDefault("defectsFound", "None reported");
        String solutionsProvided = (String) ticketData.getOrDefault("solutionsProvided", "None reported");
        
        log.debug("Completion data: ID={}, Company={}, Screen={}, Location={}, Worker={}", 
                 ticketId, companyName, screenName, screenLocation, assignedWorker);
        
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("TICKET COMPLETED\n");
        emailBody.append("================\n\n");
        emailBody.append("Hello,\n\n");
        emailBody.append("We are pleased to inform you that the following ticket has been completed:\n\n");
        
        emailBody.append("TICKET DETAILS:\n");
        emailBody.append("---------------\n");
        emailBody.append("Ticket ID: #").append(ticketId).append("\n");
        emailBody.append("Title: ").append(ticketTitle).append("\n");
        emailBody.append("Company: ").append(companyName).append("\n");
        emailBody.append("Screen: ").append(screenName).append("\n");
        emailBody.append("Location: ").append(screenLocation).append("\n");
        emailBody.append("Assigned Worker: ").append(assignedWorker).append("\n");
        emailBody.append("Completed At: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n\n");
        
        emailBody.append("WORK SUMMARY:\n");
        emailBody.append("-------------\n");
        emailBody.append("Defects Found: ").append(defectsFound).append("\n");
        emailBody.append("Solutions Provided: ").append(solutionsProvided).append("\n\n");
        
        emailBody.append("Please find the detailed worker report attached as a PDF document.\n\n");
        emailBody.append("If you have any questions or concerns about the completed work, ");
        emailBody.append("please don't hesitate to contact us.\n\n");
        emailBody.append("Thank you for choosing Celebrity Systems!\n\n");
        emailBody.append("Best regards,\n");
        emailBody.append("Celebrity Systems Team");
        
        String result = emailBody.toString();
        log.debug("Completion email body built successfully (length: {} chars)", result.length());
        
        return result;
    }
}