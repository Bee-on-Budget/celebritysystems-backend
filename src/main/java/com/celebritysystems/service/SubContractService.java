package com.celebritysystems.service;

import com.celebritysystems.dto.subcontract.SubContractRequestDTO;
import com.celebritysystems.entity.SubContract;

import java.util.List;

public interface SubContractService {
    void createSubContract(SubContractRequestDTO request);

    List<SubContract> getSubContracts();
}
