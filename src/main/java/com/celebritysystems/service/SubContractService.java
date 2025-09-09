package com.celebritysystems.service;

import com.celebritysystems.dto.PaginatedResponse;
import com.celebritysystems.dto.subcontract.SubContractRequestDTO;
import com.celebritysystems.dto.subcontract.SubContractResponseDTO;
import com.celebritysystems.entity.SubContract;

import java.util.List;

public interface SubContractService {
    void createSubContract(SubContractRequestDTO request);

    void deleteSubContract(Long id);

    PaginatedResponse<SubContract> getSubContracts(Integer page);

    void updateSubContract(Long id, SubContractRequestDTO request);
       SubContract getSubContractById(Long id);
    List<SubContract> getSubContractsByControllerCompanyId(Long controllerCompanyId);
    List<SubContract> getSubContractsByContractId(Long contractId);
    List<SubContractResponseDTO> getAllSubContractsWithNames();
}
