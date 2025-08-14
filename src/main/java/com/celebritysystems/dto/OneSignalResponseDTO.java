package com.celebritysystems.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OneSignalResponseDTO {
    
    private String id;
    private Integer recipients;
    private Object errors;
    
    public OneSignalResponseDTO() {}
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Integer getRecipients() {
        return recipients;
    }
    
    public void setRecipients(Integer recipients) {
        this.recipients = recipients;
    }
    
    public Object getErrors() {
        return errors;
    }
    
    public void setErrors(Object errors) {
        this.errors = errors;
    }
    
    public boolean isSuccessful() {
        return errors == null;
    }
    
    @Override
    public String toString() {
        return "OneSignalResponseDTO{" +
                "id='" + id + '\'' +
                ", recipients=" + recipients +
                ", errors=" + errors +
                '}';
    }
}
