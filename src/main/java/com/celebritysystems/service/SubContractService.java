package com.celebritysystems.service;

import com.celebritysystems.dto.PaginatedResponse;
import com.celebritysystems.dto.ScreenResponse;
import com.celebritysystems.dto.subcontract.SubContractRequestDTO;
import com.celebritysystems.entity.SubContract;

import java.util.List;

public interface SubContractService {
    void createSubContract(SubContractRequestDTO request);

    void deleteSubContract(Long id);

    PaginatedResponse<SubContract> getSubContracts(Integer page);
}
