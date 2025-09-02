package com.celebritysystems.dto;

import java.time.LocalDateTime;

import com.celebritysystems.entity.enums.RoleInSystem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String email;
    private String username;
    private String fullName;
    private String playerId;
    private RoleInSystem role;
    private Boolean canRead;
    private Boolean canEdit;
    private Long companyId;
    private String companyName;
    private String companyType;
    private String companyEmail;
    private String companyLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
