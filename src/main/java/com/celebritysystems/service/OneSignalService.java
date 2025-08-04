package com.celebritysystems.service;

import java.util.List;
import java.util.Map;

import com.celebritysystems.dto.OneSignalResponseDTO;

public interface OneSignalService {
    
    /**
     * Send notification to all users
     */
    OneSignalResponseDTO sendToAll(String title, String message);
    
    /**
     * Send notification to specific user
     */
    OneSignalResponseDTO sendToUser(String title, String message, String playerId);
    
    /**
     * Send notification to multiple users
     */
    OneSignalResponseDTO sendToUsers(String title, String message, List<String> playerIds);
    
    /**
     * Send notification with custom data
     */
    OneSignalResponseDTO sendWithData(String title, String message, Map<String, Object> data, List<String> playerIds);
}