package com.celebritysystems.dto.subcontract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubContractRequestDTO {
    private Long mainCompanyId;
    private Long controllerCompanyId;
    private Long contractId;
    private LocalDate createdAt;
    private LocalDate expiredAt;
}
