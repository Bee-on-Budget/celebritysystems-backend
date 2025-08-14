package com.celebritysystems.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.celebritysystems.service.OneSignalService; 

import com.celebritysystems.config.OneSignalConfig;
import com.celebritysystems.dto.OneSignalResponseDTO;

import java.util.*;

@Service
public class OneSignalServiceImpl implements OneSignalService {
    
    private final RestTemplate restTemplate;
    private final OneSignalConfig config;
    private final Logger logger = LoggerFactory.getLogger(OneSignalServiceImpl.class);
    
    public OneSignalServiceImpl(RestTemplate restTemplate, OneSignalConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }
    
    @Override
    public OneSignalResponseDTO sendToAll(String title, String message) {
        Map<String, Object> payload = createBasePayload(title, message);
        payload.put("included_segments", Arrays.asList("All"));
        return sendNotification(payload);
    }
    
    @Override
    public OneSignalResponseDTO sendToUser(String title, String message, String playerId) {
        return sendToUsers(title, message, Arrays.asList(playerId));
    }
    
    @Override
    public OneSignalResponseDTO sendToUsers(String title, String message, List<String> playerIds) {
        // Debug each player ID
        for (String playerId : playerIds) {
            debugPlayerStatus(playerId);
        }
        
        // For now, let's try sending without validation to see OneSignal's response
        Map<String, Object> payload = createBasePayload(title, message);
        payload.put("include_player_ids", playerIds);
        return sendNotification(payload);
    }
    
    @Override
    public OneSignalResponseDTO sendWithData(String title, String message, Map<String, Object> data, List<String> playerIds) {
        // Debug each player ID
        for (String playerId : playerIds) {
            debugPlayerStatus(playerId);
        }
        
        Map<String, Object> payload = createBasePayload(title, message);
        payload.put("include_player_ids", playerIds);
        payload.put("data", data);
        return sendNotification(payload);
    }
    
    /**
     * Debug player status with detailed logging
     */
    public void debugPlayerStatus(String playerId) {
        try {
            logger.info("=== DEBUGGING PLAYER ID: {} ===", playerId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + config.getApiKey());
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            String url = config.getBaseUrl() + "/players/" + playerId + "?app_id=" + config.getAppId();
            
            logger.info("Making request to: {}", url);
            logger.info("Using App ID: {}", config.getAppId());
            logger.info("Using API Key: {}***", config.getApiKey().substring(0, Math.min(8, config.getApiKey().length())));
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class);
            
            logger.info("Response Status: {}", response.getStatusCode());
            
            if (response.getBody() != null) {
                Map<String, Object> playerData = response.getBody();
                logger.info("Player Data: {}", playerData);
                
                // Check specific fields
                Object validPlayer = playerData.get("valid_player");
                Object testType = playerData.get("test_type");
                Object identifier = playerData.get("identifier");
                Object deviceType = playerData.get("device_type");
                Object lastActive = playerData.get("last_active");
                Object createdAt = playerData.get("created_at");
                
                logger.info("  - valid_player: {}", validPlayer);
                logger.info("  - test_type: {}", testType);
                logger.info("  - identifier: {}", identifier);
                logger.info("  - device_type: {}", deviceType);
                logger.info("  - last_active: {}", lastActive);
                logger.info("  - created_at: {}", createdAt);
                
                // Check if any errors in response
                Object errors = playerData.get("errors");
                if (errors != null) {
                    logger.warn("  - ERRORS: {}", errors);
                }
            } else {
                logger.warn("Empty response body");
            }
            
        } catch (Exception e) {
            logger.error("Error debugging player ID {}: {}", playerId, e.getMessage(), e);
        }
        
        logger.info("=== END DEBUG FOR PLAYER ID: {} ===", playerId);
    }
    
    /**
     * Get all players with detailed info
     */
    public void debugAllPlayers() {
        try {
            logger.info("=== FETCHING ALL PLAYERS ===");
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + config.getApiKey());
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            String url = config.getBaseUrl() + "/players?app_id=" + config.getAppId() + "&limit=50";
            
            logger.info("Making request to: {}", url);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = response.getBody();
                logger.info("Response: {}", data);
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> players = (List<Map<String, Object>>) data.get("players");
                
                if (players != null) {
                    logger.info("Found {} players", players.size());
                    for (int i = 0; i < Math.min(5, players.size()); i++) {
                        Map<String, Object> player = players.get(i);
                        logger.info("Player {}: ID={}, valid={}, device_type={}, last_active={}", 
                            i+1, 
                            player.get("id"), 
                            player.get("valid_player"),
                            player.get("device_type"),
                            player.get("last_active"));
                    }
                } else {
                    logger.warn("No players found in response");
                }
            }
            
        } catch (Exception e) {
            logger.error("Error fetching all players", e);
        }
        
        logger.info("=== END ALL PLAYERS DEBUG ===");
    }
    
    /**
     * Test configuration
     */
    public void debugConfiguration() {
        logger.info("=== CONFIGURATION DEBUG ===");
        logger.info("App ID: {}", config.getAppId());
        logger.info("API Key: {}***", config.getApiKey() != null ? config.getApiKey().substring(0, Math.min(8, config.getApiKey().length())) : "NULL");
        logger.info("Base URL: {}", config.getBaseUrl());
        logger.info("=== END CONFIGURATION DEBUG ===");
    }
    
    private Map<String, Object> createBasePayload(String title, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("app_id", config.getAppId());
        
        // Message content
        Map<String, String> contents = new HashMap<>();
        contents.put("en", message);
        payload.put("contents", contents);
        
        // Title
        if (title != null && !title.trim().isEmpty()) {
            Map<String, String> headings = new HashMap<>();
            headings.put("en", title);
            payload.put("headings", headings);
        }
        
        return payload;
    }
    
    private OneSignalResponseDTO sendNotification(Map<String, Object> payload) {
        try {
            logger.info("Sending notification with payload: {}", payload);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + config.getApiKey());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            String url = config.getBaseUrl() + "/notifications";
            
            ResponseEntity<OneSignalResponseDTO> response = restTemplate.postForEntity(
                url, entity, OneSignalResponseDTO.class);
            
            OneSignalResponseDTO responseBody = response.getBody();
            
            if (responseBody != null) {
                logger.info("OneSignal response: {}", responseBody);
                
                if (responseBody.getErrors() != null) {
                    logger.warn("OneSignal returned errors: {}", responseBody.getErrors());
                } else {
                    logger.info("Notification sent successfully. Recipients: {}", responseBody.getRecipients());
                }
            }
            
            return responseBody;
            
        } catch (Exception e) {
            logger.error("Error sending OneSignal notification", e);
            OneSignalResponseDTO errorResponse = new OneSignalResponseDTO();
            errorResponse.setErrors("Failed to send notification: " + e.getMessage());
            return errorResponse;
        }
    }
}
