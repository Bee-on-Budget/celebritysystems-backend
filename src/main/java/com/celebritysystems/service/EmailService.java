package com.celebritysystems.service;

import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

public interface EmailService {
    
    void sendSimpleEmail(String to, String subject, String text);
    
    void sendTicketAssignmentEmail(String toEmail, String workerName, String ticketTitle, 
                                 String ticketDescription, Map<String, Object> ticketData);
    
    void sendTicketStatusUpdateEmail(List<String> toEmails, String ticketTitle, 
                                   String previousStatus, String newStatus, 
                                   Map<String, Object> ticketData);
    
    /**
     * Send ticket completion email with worker report PDF attachment
     */
    void sendTicketCompletionEmailWithPdf(List<String> toEmails, String ticketTitle, 
                                         Map<String, Object> ticketData, 
                                         Resource pdfAttachment, String pdfFileName);
    
    /**
     * Send email with attachment
     */
    void sendEmailWithAttachment(String to, String subject, String text, 
                                Resource attachment, String attachmentFileName);
    
    /**
     * Send email with attachment to multiple recipients
     */
    void sendEmailWithAttachment(List<String> toEmails, String subject, String text, 
                                Resource attachment, String attachmentFileName);
}