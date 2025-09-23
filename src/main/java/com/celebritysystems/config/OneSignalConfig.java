package com.celebritysystems.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "onesignal")
public class OneSignalConfig {
    
    private String appId;
    private String apiKey;
    private String baseUrl = "https://onesignal.com/api/v1";

    // Mail configuration from environment variables
    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:false}")
    private boolean starttlsEnable;

    @Value("${spring.mail.properties.mail.smtp.starttls.required:false}")
    private boolean starttlsRequired;

    @Value("${spring.mail.properties.mail.smtp.ssl.enable:false}")
    private boolean sslEnable;

    @Value("${spring.mail.properties.mail.smtp.ssl.required:false}")
    private boolean sslRequired;

    @Value("${spring.mail.properties.mail.debug:false}")
    private boolean mailDebug;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", String.valueOf(smtpAuth));
        
        // Configure STARTTLS or SSL based on environment variables
        if (starttlsEnable) {
            props.put("mail.smtp.starttls.enable", String.valueOf(starttlsEnable));
            props.put("mail.smtp.starttls.required", String.valueOf(starttlsRequired));
        }
        
        if (sslEnable) {
            props.put("mail.smtp.ssl.enable", String.valueOf(sslEnable));
            props.put("mail.smtp.ssl.required", String.valueOf(sslRequired));
        }
        
        props.put("mail.debug", String.valueOf(mailDebug));
        
        return mailSender;
    }
    
    // OneSignal getters and setters
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