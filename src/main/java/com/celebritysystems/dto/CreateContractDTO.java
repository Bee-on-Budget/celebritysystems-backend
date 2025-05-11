package com.celebritysystems.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateContractDTO {
    private String info;
    private LocalDateTime expiredAt;
    private Long companyId;
    private Long screenId;
}