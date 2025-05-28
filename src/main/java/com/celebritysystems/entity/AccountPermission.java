package com.celebritysystems.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class AccountPermission {
    private Long accountId;
    private boolean canEdit;
    private boolean canRead;
}
