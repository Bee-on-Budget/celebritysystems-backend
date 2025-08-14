package com.celebritysystems.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateContractDTO {
    private String info;
    private LocalDateTime expiredAt;
}
