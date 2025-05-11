package com.celebritysystems.service;

import com.celebritysystems.entity.Contract;
import java.util.List;
import java.util.Optional;

public interface ContractService {
    Contract createContract(Contract contract);
    Contract getContractById(Long id);
    List<Contract> getContractsByCompany(Long companyId);
    List<Contract> getContractsByScreen(Long screenId);
    Optional<Contract> getCurrentContractForScreen(Long screenId);
    Contract updateContract(Long id, Contract contractDetails);
    void deleteContract(Long id);
    boolean contractExistsForCompanyAndScreen(Long companyId, Long screenId);
}