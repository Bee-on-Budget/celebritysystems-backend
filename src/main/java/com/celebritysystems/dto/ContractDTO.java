package com.celebritysystems.dto;


import lombok.Data;
import java.time.LocalDateTime;

import com.celebritysystems.entity.enums.ContractType;
import com.celebritysystems.entity.enums.OperatorType;
import com.celebritysystems.entity.enums.SupplyType;

@Data
public class ContractDTO {
    private Long id;
    private String info;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private Long companyId;
    private Long screenId;
    private SupplyType supplyType;
    private OperatorType operatorType;
    private String accountName;
    private ContractType durationType;
    private Double contractValue;
}