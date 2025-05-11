package com.celebritysystems.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ContractDTO {
    private Long id;
    private String info;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private Long companyId;
    private Long screenId;
}