package com.celebritysystems.controller;

import com.celebritysystems.entity.Contract;
import com.celebritysystems.entity.repository.ContractRepository;
import com.celebritysystems.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {
    @Autowired
    private ContractRepository contractRepository;

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping
    public ResponseEntity<Contract> createContract(@RequestBody Contract contract) {
        Contract createdContract = contractService.createContract(contract);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContract);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContractById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    @GetMapping
    public ResponseEntity<List<Contract>> getAllContracts() {
        return ResponseEntity.ok(contractRepository.findAll()); // Or implement getAll in service
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
}