package com.celebritysystems.dto.auth;

import com.celebritysystems.entity.enums.RoleInSystem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public  class UserRegistrationDto {
    private String name;
    private String email;
    private String username;
    private String password;
    private List<String> roles;


}