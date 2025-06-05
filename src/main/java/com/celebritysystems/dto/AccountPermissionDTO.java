package com.celebritysystems.dto;

import lombok.Data;

@Data
public class AccountPermissionDTO {
    private String name;
    private boolean canEdit;
    private boolean canRead;
}
