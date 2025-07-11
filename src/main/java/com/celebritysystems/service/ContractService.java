package com.celebritysystems.service;

import com.celebritysystems.dto.ContractResponseDTO;
import com.celebritysystems.dto.CreateContractDTO;
import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
import com.celebritysystems.entity.Contract;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

public interface ContractService {
    Contract createContract(Contract contract);

    Contract createContractFromDTO(CreateContractDTO dto);

    Contract getContractById(Long id);

    List<Contract> getContractsByCompany(Long companyId);

    List<Contract> getContractsByScreen(Long screenId);

    Optional<Contract> getCurrentContractForScreen(Long screenId);

    Contract updateContract(Long id, Contract contractDetails);

    void deleteContract(Long id);

    boolean contractExistsForCompanyAndScreen(Long companyId, Long screenId);

    long getContractCountByMonthAndYear(int month, int year);

    List<MonthlyStats> getMonthlyContractStats();

    List<AnnualStats> getAnnualContractStats();

    List<Contract> getContractsByCompanyName(String companyName);

    double getTotalContractValue();

    public List<ContractResponseDTO> getAllContractsWithNames();

    Page<Contract> findAllPaginated(int page, int size);

    Page<ContractResponseDTO> getAllContractsWithNamesPaginated(int page, int size);
}
