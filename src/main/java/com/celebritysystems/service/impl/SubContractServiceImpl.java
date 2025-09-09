package com.celebritysystems.service.impl;

import com.celebritysystems.dto.PaginatedResponse;
import com.celebritysystems.dto.subcontract.SubContractRequestDTO;
import com.celebritysystems.dto.subcontract.SubContractResponseDTO;
import com.celebritysystems.entity.Company;
import com.celebritysystems.entity.Contract;
import com.celebritysystems.entity.SubContract;
import com.celebritysystems.repository.CompanyRepository;
import com.celebritysystems.repository.ContractRepository;
import com.celebritysystems.repository.SubContractRepository;
import com.celebritysystems.service.SubContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public void deleteSubContract(Long id) {
        if (!subContractRepository.existsById(id)) {
            throw new RuntimeException("SubContract not found with id: " + id);
        }
        subContractRepository.deleteById(id);
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
                .createdAt(request.getCreatedAt())
                .expiredAt(request.getExpiredAt())
                .build();

        subContractRepository.save(subContract);
    }

    @Override
    public PaginatedResponse<SubContract> getSubContracts(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());

        Page<SubContract> subContractPage = subContractRepository.findAll(pageable);
        List<SubContract> subContractList = subContractPage.stream().toList();

        PaginatedResponse<SubContract> response = new PaginatedResponse<>();
        response.setContent(subContractList);
        response.setPageNumber(subContractPage.getNumber());
        response.setPageSize(subContractPage.getSize());
        response.setTotalElements(subContractPage.getTotalElements());
        response.setTotalPages(subContractPage.getTotalPages());
        response.setHasNext(subContractPage.hasNext());
        response.setHasPrevious(subContractPage.hasPrevious());

        return response;
    }

    @Override
    public void updateSubContract(Long id, SubContractRequestDTO request) {
        SubContract existingSubContract = subContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubContract not found with id: " + id));

        Company mainCompany = companyRepository.findById(request.getMainCompanyId()).orElseThrow(() -> new RuntimeException("mainCompany not found with id: " + request.getMainCompanyId()));
        Company controllerCompany = companyRepository.findById(request.getControllerCompanyId()).orElseThrow(() -> new RuntimeException("controllerCompany not found with id: " + request.getControllerCompanyId()));
        Contract contract = contractRepository.findById(request.getContractId()).orElseThrow(() -> new RuntimeException("Contract not found with id: " + request.getContractId()));

        existingSubContract.setMainCompany(mainCompany);
        existingSubContract.setControllerCompany(controllerCompany);
        existingSubContract.setContract(contract);
        existingSubContract.setCreatedAt(request.getCreatedAt());
        existingSubContract.setExpiredAt(request.getExpiredAt());

        subContractRepository.save(existingSubContract);
    }

    @Override
    public SubContract getSubContractById(Long id) {
        return subContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubContract not found with id: " + id));
    }

    @Override
    public List<SubContract> getSubContractsByControllerCompanyId(Long controllerCompanyId) {
        // Verify controller company exists
        if (!companyRepository.existsById(controllerCompanyId)) {
            throw new RuntimeException("Controller company not found with id: " + controllerCompanyId);
        }
        return subContractRepository.findByControllerCompanyId(controllerCompanyId);
    }

    @Override
    public List<SubContract> getSubContractsByContractId(Long contractId) {
        // Verify contract exists
        if (!contractRepository.existsById(contractId)) {
            throw new RuntimeException("Contract not found with id: " + contractId);
        }
        return subContractRepository.findByContractId(contractId);
    }

    @Override
    public List<SubContractResponseDTO> getAllSubContractsWithNames() {
        List<SubContract> subContracts = subContractRepository.findAllWithCompanyNames();
        return subContracts.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private SubContractResponseDTO convertToResponseDTO(SubContract subContract) {
        return SubContractResponseDTO.builder()
                .id(subContract.getId().longValue())
                .mainCompanyName(subContract.getMainCompany().getName())
                .controllerCompanyName(subContract.getControllerCompany().getName())
                .contractId(subContract.getContract().getId())
                .createdAt(subContract.getCreatedAt())
                .expiredAt(subContract.getExpiredAt())
                .build();
    }
}