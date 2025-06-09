package com.celebritysystems.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ContractResponseDTO {
    private Long id;
    private String info;
    private LocalDateTime startContractAt;
    private LocalDateTime expiredAt;
    private String companyName;
    private String accountName;
    private Double contractValue;
    private String durationType;
    private String operatorType;
    private String supplyType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> screenNames;
    private List<AccountPermissionDTO> accountPermissions;
}
