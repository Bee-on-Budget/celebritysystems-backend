package com.celebritysystems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;
    
    @NotBlank(message = "Phone number is required")
    private String phone;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;
    
    @Size(max = 200, message = "Location must be less than 200 characters")
    private String location;
}