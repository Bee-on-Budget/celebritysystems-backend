package com.celebritysystems.service.impl;

import com.celebritysystems.entity.Contract;
import com.celebritysystems.repository.ContractRepository;
import com.celebritysystems.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Contract> findById(Long id) {
        return contractRepository.findById(id);
    }

    @Override
    public Contract save(Contract contract) {
        return contractRepository.save(contract);
    }

    @Override
    public void deleteById(Long id) {
        contractRepository.deleteById(id);
    }
} 