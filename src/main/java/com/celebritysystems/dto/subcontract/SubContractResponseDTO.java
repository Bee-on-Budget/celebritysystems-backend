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
public class SubContractResponseDTO {
    private Long id;
    private String mainCompanyName;
    private String controllerCompanyName;
    private Long contractId;
    private LocalDate createdAt;
    private LocalDate expiredAt;
}
