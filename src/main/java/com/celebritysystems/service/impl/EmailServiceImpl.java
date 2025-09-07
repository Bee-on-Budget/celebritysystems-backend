package com.celebritysystems.service.impl;

import com.celebritysystems.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send simple email to {}: {}", to, e.getMessage(), e);
        }
    }
    
    @Override
    public void sendTicketAssignmentEmail(String toEmail, String workerName, String ticketTitle, 
                                        String ticketDescription, Map<String, Object> ticketData) {
        try {
            String subject = "New Ticket Assigned - " + ticketTitle;
            
            String emailBody = buildTicketAssignmentText(workerName, ticketTitle, 
                                                       ticketDescription, ticketData);
            
            sendSimpleEmail(toEmail, subject, emailBody);
            log.info("Ticket assignment email sent to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send ticket assignment email to {}: {}", toEmail, e.getMessage(), e);
        }
    }
    
    @Override
    public void sendTicketStatusUpdateEmail(List<String> toEmails, String ticketTitle, 
                                          String previousStatus, String newStatus, 
                                          Map<String, Object> ticketData) {
        try {
            String subject = "Ticket Status Update - " + ticketTitle;
            
            String emailBody = buildTicketStatusUpdateText(ticketTitle, previousStatus, 
                                                         newStatus, ticketData);
            
            for (String email : toEmails) {
                sendSimpleEmail(email, subject, emailBody);
            }
            log.info("Ticket status update email sent to {} recipients", toEmails.size());
            
        } catch (Exception e) {
            log.error("Failed to send ticket status update email: {}", e.getMessage(), e);
        }
    }
    
    private String buildTicketAssignmentText(String workerName, String ticketTitle, 
                                           String ticketDescription, Map<String, Object> ticketData) {
        
        String companyName = (String) ticketData.getOrDefault("companyName", "N/A");
        String screenName = (String) ticketData.getOrDefault("screenName", "N/A");
        String screenLocation = (String) ticketData.getOrDefault("screenLocation", "N/A");
        String createdBy = (String) ticketData.getOrDefault("createdBy", "N/A");
        String ticketId = (String) ticketData.getOrDefault("ticketId", "N/A");
        
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
        
        return emailBody.toString();
    }
    
    private String buildTicketStatusUpdateText(String ticketTitle, String previousStatus, 
                                             String newStatus, Map<String, Object> ticketData) {
        
        String companyName = (String) ticketData.getOrDefault("companyName", "N/A");
        String screenName = (String) ticketData.getOrDefault("screenName", "N/A");
        String assignedWorker = (String) ticketData.getOrDefault("assignedWorker", "N/A");
        String ticketId = (String) ticketData.getOrDefault("ticketId", "N/A");
        
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
        
        return emailBody.toString();
    }
}