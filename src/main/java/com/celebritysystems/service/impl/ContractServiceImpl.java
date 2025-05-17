package com.celebritysystems.service.impl;

import com.celebritysystems.entity.Contract;
import com.celebritysystems.entity.Screen;
import com.celebritysystems.entity.repository.ContractRepository;
import com.celebritysystems.entity.repository.ScreenRepository;
import com.celebritysystems.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;

    @Override
    public Contract createContract(Contract contract) {
//        Optional<Screen> screen = screenRepository.findById(contract.getScreenId());
        List<Contract> contractCheck = contractRepository.findByScreenId(contract.getScreenId());

        //for loop if the contract expired date > now . the screen with id (x) already have an ongoing contract now.
        for (Contract existingContract : contractCheck) {
            if (existingContract.getExpiredAt().isAfter(LocalDateTime.now())) {
                throw new IllegalStateException("Screen with id " + contract.getScreenId() + " already has an ongoing contract.");
            }
        }

        if (contract.getCompanyId() != null && contract.getScreenId() != null
            && contractRepository.existsByCompanyIdAndScreenId(contract.getCompanyId(), contract.getScreenId())) {
            throw new IllegalArgumentException("Active contract already exists for this company-screen combination");
        }

        if (contract.getCreatedAt() == null) {
            contract.setCreatedAt(LocalDateTime.now());
        }

        return contractRepository.save(contract);
    }

    @Override
    public Contract getContractById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + id));
    }

    @Override
    public List<Contract> getContractsByCompany(Long companyId) {
        return contractRepository.findByCompanyId(companyId);
    }

    @Override
    public List<Contract> getContractsByScreen(Long screenId) {
        return contractRepository.findByScreenId(screenId);
    }

    @Override
    public Optional<Contract> getCurrentContractForScreen(Long screenId) {
        return contractRepository.findFirstByScreenIdOrderByCreatedAtDesc(screenId);
    }

    @Override
    public Contract updateContract(Long id, Contract contractDetails) {
        Contract existingContract = getContractById(id);

        if (contractDetails.getInfo() != null) {
            existingContract.setInfo(contractDetails.getInfo());
        }
        if (contractDetails.getExpiredAt() != null) {
            existingContract.setExpiredAt(contractDetails.getExpiredAt());
        }

        return contractRepository.save(existingContract);
    }

    @Override
    public void deleteContract(Long id) {
        if (!contractRepository.existsById(id)) {
            throw new IllegalArgumentException("Contract not found with id: " + id);
        }
        contractRepository.deleteById(id);
    }

    @Override
    public boolean contractExistsForCompanyAndScreen(Long companyId, Long screenId) {
        return contractRepository.existsByCompanyIdAndScreenId(companyId, screenId);
    }
}