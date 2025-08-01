package com.celebritysystems.service.impl;

import com.celebritysystems.dto.AccountPermissionDTO;
import com.celebritysystems.dto.ContractResponseDTO;
import com.celebritysystems.dto.CreateContractDTO;
import com.celebritysystems.dto.ScreenResponse;
import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
import com.celebritysystems.entity.AccountPermission;
import com.celebritysystems.entity.Contract;
import com.celebritysystems.entity.enums.ContractType;
import com.celebritysystems.entity.enums.OperatorType;
import com.celebritysystems.entity.enums.SupplyType;
import com.celebritysystems.repository.CompanyRepository;
import com.celebritysystems.repository.ContractRepository;
import com.celebritysystems.service.ContractService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
private final CompanyRepository companyRepository;
private final ScreenServiceImpl screenService;
    @Override
    public Contract createContract(Contract contract) {
        if (contract.getScreenIds() != null) {
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

            contract.setScreenIds(validScreenIds);
        }

        if (contract.getStartContractAt() == null) {
            contract.setStartContractAt(LocalDateTime.now());
        }

        return contractRepository.save(contract);
    }

    @Override
    public Contract createContractFromDTO(CreateContractDTO dto) {
        List<AccountPermission> permissions = dto.getAccountPermissions() != null
                ? dto.getAccountPermissions().stream()
                    .map(this::mapToEntity)
                    .distinct() // Ensures only unique entries
                    .collect(Collectors.toList())
                : List.of();
    
        List<Long> screenIds = dto.getScreenIds() != null
                ? dto.getScreenIds().stream().filter(Objects::nonNull).distinct().collect(Collectors.toList())
                : List.of();
    
        Contract contract = Contract.builder()
                .info(dto.getInfo())
                .startContractAt(dto.getStartContractAt())
                .expiredAt(dto.getExpiredAt())
                .companyId(dto.getCompanyId())
                .supplyType(SupplyType.valueOf(dto.getSupplyType().toUpperCase()))
                .operatorType(OperatorType.valueOf(dto.getOperatorType().toUpperCase()))
                .accountName(dto.getAccountName())
                .durationType(ContractType.valueOf(dto.getDurationType().toUpperCase()))
                .contractValue(dto.getContractValue())
                .screenIds(screenIds)
                .accountPermissions(permissions)
                .build();
    
        return createContract(contract);
    }
    

    private AccountPermission mapToEntity(AccountPermissionDTO dto) {
        AccountPermission entity = new AccountPermission();
        entity.setName(dto.getName());
        entity.setCanEdit(dto.isCanEdit());
        entity.setCanRead(dto.isCanRead());
        return entity;
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
    public List<Contract> getContractsByCompanyName(String companyName) {
        return contractRepository.findByCompanyNameContainingIgnoreCase(companyName);
    }
    
@Override
public double getTotalContractValue() {
    Double result = contractRepository.sumAllContractValues();
    return result != null ? result : 0.0;
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

    @Override
    public long getContractCountByMonthAndYear(int month, int year) {
        return contractRepository.countByMonthAndYear(month, year);
    }

    @Override
    public List<MonthlyStats> getMonthlyContractStats() {
        return contractRepository.getMonthlyContractRegistrationStats()
                .stream()
                .map(record -> new MonthlyStats(
                        ((Number) record[0]).intValue(),
                        ((Number) record[1]).intValue(),
                        ((Number) record[2]).longValue()))
                .toList();
    }

    @Override
    public List<AnnualStats> getAnnualContractStats() {
        return contractRepository.getAnnualContractRegistrationStats()
                .stream()
                .map(record -> new AnnualStats(
                        ((Number) record[0]).intValue(),
                        ((Number) record[1]).longValue()))
                .toList();
    }
    @Override
    public List<ContractResponseDTO> getAllContractsWithNames() {
        List<Contract> contracts = contractRepository.findAll();
    
        return contracts.stream().map(contract -> {
            ContractResponseDTO dto = new ContractResponseDTO();
            dto.setId(contract.getId());
            dto.setInfo(contract.getInfo());
            dto.setStartContractAt(contract.getStartContractAt());
            dto.setExpiredAt(contract.getExpiredAt());
            dto.setAccountName(contract.getAccountName());
            dto.setContractValue(contract.getContractValue());
    
            // Convert enums to String safely
            dto.setDurationType(contract.getDurationType() != null ? contract.getDurationType().name() : null);
            dto.setOperatorType(contract.getOperatorType() != null ? contract.getOperatorType().name() : null);
            dto.setSupplyType(contract.getSupplyType() != null ? contract.getSupplyType().name() : null);
    
            dto.setCreatedAt(contract.getCreatedAt());
            dto.setUpdatedAt(contract.getUpdatedAt());
    
            // Map AccountPermission -> AccountPermissionDTO
            List<AccountPermissionDTO> permissionDTOs = contract.getAccountPermissions() != null
                    ? contract.getAccountPermissions().stream()
                        .map(permission -> {
                            AccountPermissionDTO dtoPermission = new AccountPermissionDTO();
                            dtoPermission.setName(permission.getName());
                            dtoPermission.setCanRead(permission.isCanRead());
                            dtoPermission.setCanEdit(permission.isCanEdit());
                            return dtoPermission;
                        }).collect(Collectors.toList())
                    : List.of();
    
            dto.setAccountPermissions(permissionDTOs);
    
            // Fetch company name
            companyRepository.findById(contract.getCompanyId())
                    .ifPresent(company -> dto.setCompanyName(company.getName()));
    
            // Fetch screen names
            List<String> screenNames = contract.getScreenIds() != null
                    ? contract.getScreenIds().stream()
                        .map(screenId -> screenService.getScreenById(screenId)
                                .map(ScreenResponse::getName)
                                .orElse("Unknown"))
                        .collect(Collectors.toList())
                    : List.of();
    
            dto.setScreenNames(screenNames);
    
            return dto;
        }).collect(Collectors.toList());
    }
    @Override
public Page<Contract> findAllPaginated(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return contractRepository.findAll(pageable);
}

@Override
public Page<ContractResponseDTO> getAllContractsWithNamesPaginated(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<Contract> contracts = contractRepository.findAll(pageable);
    
    return contracts.map(contract -> {
        ContractResponseDTO dto = new ContractResponseDTO();
        dto.setId(contract.getId());
        dto.setInfo(contract.getInfo());
        dto.setStartContractAt(contract.getStartContractAt());
        dto.setExpiredAt(contract.getExpiredAt());
        dto.setAccountName(contract.getAccountName());
        dto.setContractValue(contract.getContractValue());
        
        // Convert enums to String safely
        dto.setDurationType(contract.getDurationType() != null ? contract.getDurationType().name() : null);
        dto.setOperatorType(contract.getOperatorType() != null ? contract.getOperatorType().name() : null);
        dto.setSupplyType(contract.getSupplyType() != null ? contract.getSupplyType().name() : null);
        
        dto.setCreatedAt(contract.getCreatedAt());
        dto.setUpdatedAt(contract.getUpdatedAt());
        
        // Map AccountPermission -> AccountPermissionDTO
        List<AccountPermissionDTO> permissionDTOs = contract.getAccountPermissions() != null
                ? contract.getAccountPermissions().stream()
                    .map(permission -> {
                        AccountPermissionDTO dtoPermission = new AccountPermissionDTO();
                        dtoPermission.setName(permission.getName());
                        dtoPermission.setCanRead(permission.isCanRead());
                        dtoPermission.setCanEdit(permission.isCanEdit());
                        return dtoPermission;
                    }).collect(Collectors.toList())
                : List.of();
        
        dto.setAccountPermissions(permissionDTOs);
        
        // Fetch company name
        companyRepository.findById(contract.getCompanyId())
                .ifPresent(company -> dto.setCompanyName(company.getName()));
        
        // Fetch screen names
        List<String> screenNames = contract.getScreenIds() != null
                ? contract.getScreenIds().stream()
                    .map(screenId -> screenService.getScreenById(screenId)
                            .map(ScreenResponse::getName)
                            .orElse("Unknown"))
                    .collect(Collectors.toList())
                : List.of();
        
        dto.setScreenNames(screenNames);
        
        return dto;
    });
}
    
}
