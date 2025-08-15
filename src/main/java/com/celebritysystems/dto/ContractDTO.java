package com.celebritysystems.dto;

import com.celebritysystems.entity.enums.ContractType;
import com.celebritysystems.entity.enums.OperatorType;
import com.celebritysystems.entity.enums.SupplyType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ContractDTO {
    private Long id;
    private String info;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private Long companyId;
    private SupplyType supplyType;
    private OperatorType operatorType;
    private String accountName;
    private ContractType durationType;
    private Double contractValue;
    private List<Long> screenIds;
    private List<AccountPermissionDTO> accountPermissions;
}
