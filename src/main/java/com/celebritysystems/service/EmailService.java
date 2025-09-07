package com.celebritysystems.service;

import java.util.List;
import java.util.Map;

public interface EmailService {
    
    void sendSimpleEmail(String to, String subject, String text);
    
    void sendTicketAssignmentEmail(String toEmail, String workerName, String ticketTitle, 
                                  String ticketDescription, Map<String, Object> ticketData);
    
    void sendTicketStatusUpdateEmail(List<String> toEmails, String ticketTitle, 
                                   String previousStatus, String newStatus, 
                                   Map<String, Object> ticketData);
}