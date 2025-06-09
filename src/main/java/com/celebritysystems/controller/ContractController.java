package com.celebritysystems.controller;

import com.celebritysystems.dto.ContractResponseDTO;
import com.celebritysystems.dto.CreateContractDTO;
import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
import com.celebritysystems.entity.Contract;
import com.celebritysystems.repository.ContractRepository;
import com.celebritysystems.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Slf4j
public class ContractController {
    private final ContractRepository contractRepository;
    private final ContractService contractService;

    @PostMapping
    public ResponseEntity<Contract> createContract(@RequestBody CreateContractDTO dto) {
        Contract createdContract = contractService.createContractFromDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContract);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContractById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    @GetMapping
    public ResponseEntity<List<Contract>> getAllContracts() {
        return ResponseEntity.ok(contractRepository.findAll());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Contract>> getContractsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(contractService.getContractsByCompany(companyId));
    }

    @GetMapping("/screen/{screenId}")
    public ResponseEntity<List<Contract>> getContractsByScreen(@PathVariable Long screenId) {
        return ResponseEntity.ok(contractService.getContractsByScreen(screenId));
    }

    @GetMapping("/screen/{screenId}/current")
    public ResponseEntity<Contract> getCurrentContractForScreen(@PathVariable Long screenId) {
        Optional<Contract> contract = contractService.getCurrentContractForScreen(screenId);
        return contract.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contract> updateContract(
            @PathVariable Long id,
            @RequestBody Contract contractDetails) {
        return ResponseEntity.ok(contractService.updateContract(id, contractDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkContractExists(
            @RequestParam Long companyId,
            @RequestParam Long screenId) {
        return ResponseEntity.ok(contractService.contractExistsForCompanyAndScreen(companyId, screenId));
    }

    @GetMapping("/statistic/monthly")
    public List<MonthlyStats> getMonthlyContractStats() {
        log.info("Fetching monthly contract statistics");
        return contractService.getMonthlyContractStats();
    }

    @GetMapping("/statistic/annual")
    public List<AnnualStats> getAnnualContractStats() {
        log.info("Fetching annual contract statistics");
        return contractService.getAnnualContractStats();
    }

    @GetMapping("/statistic/count")
    public long getCountByMonthAndYear(@RequestParam int month, @RequestParam int year) {
        log.info("Getting count of contract for month {} and year {}", month, year);
        return contractService.getContractCountByMonthAndYear(month, year);
    }
    @GetMapping("/with-names")
public ResponseEntity<List<ContractResponseDTO>> getAllContractsWithNames() {
    return ResponseEntity.ok(contractService.getAllContractsWithNames());
}

}
