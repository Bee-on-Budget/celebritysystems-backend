package com.celebritysystems.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateContractDTO {
    private String info;
    private LocalDateTime startContractAt;
    private LocalDateTime expiredAt;
    private Long companyId;
    private List<Long> screenIds;
    private String supplyType;
    private String operatorType;
    private String accountName;
    private String durationType;
    private Double contractValue;
    private List<AccountPermissionDTO> accountPermissions;
}
