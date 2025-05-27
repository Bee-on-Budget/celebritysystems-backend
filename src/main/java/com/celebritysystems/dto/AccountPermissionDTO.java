package com.celebritysystems.dto;

import lombok.Data;

@Data
public class AccountPermissionDTO {
    private Long accountId;
    private boolean canEdit;
    private boolean canRead;
}
