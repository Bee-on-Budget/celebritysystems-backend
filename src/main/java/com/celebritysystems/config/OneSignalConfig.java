package com.celebritysystems.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "onesignal")
public class OneSignalConfig {
    
    private String appId;
    private String apiKey;
    private String baseUrl = "https://onesignal.com/api/v1";
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    // Getters and setters
    public String getAppId() { 
        return appId; 
    }
    
    public void setAppId(String appId) { 
        this.appId = appId; 
    }
    
    public String getApiKey() { 
        return apiKey; 
    }
    
    public void setApiKey(String apiKey) { 
        this.apiKey = apiKey; 
    }
    
    public String getBaseUrl() { 
        return baseUrl; 
    }
    
    public void setBaseUrl(String baseUrl) { 
        this.baseUrl = baseUrl; 
    }
}
