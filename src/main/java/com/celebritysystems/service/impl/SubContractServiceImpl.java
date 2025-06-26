package com.celebritysystems.service.impl;

import com.celebritysystems.dto.subcontract.SubContractRequestDTO;
import com.celebritysystems.entity.Company;
import com.celebritysystems.entity.Contract;
import com.celebritysystems.entity.SubContract;
import com.celebritysystems.repository.CompanyRepository;
import com.celebritysystems.repository.ContractRepository;
import com.celebritysystems.repository.SubContractRepository;
import com.celebritysystems.service.SubContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubContractServiceImpl implements SubContractService {

    private SubContractRepository subContractRepository;
    private CompanyRepository companyRepository;
    private ContractRepository contractRepository;

    @Autowired
    public SubContractServiceImpl(SubContractRepository subContractRepository, CompanyRepository companyRepository, ContractRepository contractRepository) {
        this.subContractRepository = subContractRepository;
        this.companyRepository = companyRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    public void createSubContract(SubContractRequestDTO request) {
        Company mainCompany = companyRepository.findById(request.getMainCompanyId()).orElseThrow(() -> new RuntimeException("main company not found"));
        Company controllerCompany = companyRepository.findById(request.getControllerCompanyId()).orElseThrow(() -> new RuntimeException("controller company not found"));
        Contract contract = contractRepository.findById(request.getContractId()).orElseThrow(() -> new RuntimeException("contract not found"));

        SubContract subContract = SubContract.builder()
                .mainCompany(mainCompany)
                .controllerCompany(controllerCompany)
                .contract(contract)
                .expiredAt(request.getExpiredAt())
                .build();

        subContractRepository.save(subContract);
    }

    @Override
    public List<SubContract> getSubContracts() {
        return subContractRepository.findAll();
    }
}
