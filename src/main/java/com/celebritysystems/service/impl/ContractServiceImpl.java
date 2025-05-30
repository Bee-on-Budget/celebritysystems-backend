package com.celebritysystems.service.impl;

import com.celebritysystems.entity.Contract;
import com.celebritysystems.repository.ContractRepository;
import com.celebritysystems.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;

    @Override
    public Contract createContract(Contract contract) {
        if (contract.getScreenIds() != null) {
            // Filter out null screenIds to avoid processing invalid entries
            List<Long> validScreenIds = contract.getScreenIds()
                                                .stream()
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toList());

            if (validScreenIds.isEmpty()) {
                throw new IllegalArgumentException("Screen IDs must not be null or empty.");
            }

            for (Long screenId : validScreenIds) {
                List<Contract> existingContracts = contractRepository.findByScreenId(screenId);
                for (Contract existingContract : existingContracts) {
                    if (existingContract.getExpiredAt().isAfter(LocalDateTime.now())) {
                        throw new IllegalStateException("Screen with id " + screenId + " already has an ongoing contract.");
                    }
                }

                if (contract.getCompanyId() != null &&
                        contractRepository.existsByCompanyIdAndScreenId(contract.getCompanyId(), screenId)) {
                    throw new IllegalArgumentException("Active contract already exists for company " + contract.getCompanyId() + " and screen " + screenId);
                }
            }

            // Replace screenIds with filtered list
            contract.setScreenIds(validScreenIds);
        }

        if (contract.getStartContractAt() == null) {
            contract.setStartContractAt(LocalDateTime.now());
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
        if (contractDetails.getSupplyType() != null) {
            existingContract.setSupplyType(contractDetails.getSupplyType());
        }
        if (contractDetails.getOperatorType() != null) {
            existingContract.setOperatorType(contractDetails.getOperatorType());
        }
        if (contractDetails.getDurationType() != null) {
            existingContract.setDurationType(contractDetails.getDurationType());
        }
        if (contractDetails.getContractValue() != null) {
            existingContract.setContractValue(contractDetails.getContractValue());
        }
        if (contractDetails.getScreenIds() != null) {
            List<Long> validScreenIds = contractDetails.getScreenIds()
                                                       .stream()
                                                       .filter(Objects::nonNull)
                                                       .collect(Collectors.toList());
            existingContract.setScreenIds(validScreenIds);
        }
        if (contractDetails.getAccountPermissions() != null) {
            existingContract.setAccountPermissions(contractDetails.getAccountPermissions());
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
