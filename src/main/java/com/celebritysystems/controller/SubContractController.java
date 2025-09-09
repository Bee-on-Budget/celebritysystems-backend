package com.celebritysystems.controller;

import com.celebritysystems.dto.*;
import com.celebritysystems.dto.subcontract.SubContractRequestDTO;
import com.celebritysystems.dto.subcontract.SubContractResponseDTO;
import com.celebritysystems.entity.SubContract;
import com.celebritysystems.service.SubContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

import java.util.List;

@RestController
@RequestMapping("/api/subcontract")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class SubContractController {
    private SubContractService subContractService;

    @Autowired
    public SubContractController(SubContractService subContractService) {
        this.subContractService = subContractService;
    }

    @PostMapping()
    public ResponseEntity<?> createSubContract(@RequestBody SubContractRequestDTO request) {
        try {
            log.info("Received SubContract request: {}", request);
            subContractService.createSubContract(request);
            return ResponseEntity.ok("SubContract created successfully");

        } catch (Exception e) {
            log.error("Error creating SubContract", e);
            return ResponseEntity.status(500).body("Error creating SubContract: " + e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getSubContracts(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        try {
            log.info("Received SubContract request for page: {}", page);
            PaginatedResponse<SubContract> subContractList = subContractService.getSubContracts(page);
            return ResponseEntity.ok(subContractList);

        } catch (MultipartException e) {
            return ResponseEntity.badRequest().body("Invalid file upload");
        } catch (Exception e) {
            log.error("Error viewing SubContract", e);
            return ResponseEntity.status(500).body("Error viewing SubContract: " + e.getMessage());
        }
    }

    // NEW ENDPOINT: Get subcontract by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSubContractById(@PathVariable Long id) {
        try {
            log.info("Fetching SubContract with id: {}", id);
            SubContract subContract = subContractService.getSubContractById(id);
            return ResponseEntity.ok(subContract);
        } catch (Exception e) {
            log.error("Error fetching SubContract with id: {}", id, e);
            return ResponseEntity.status(500).body("Error fetching SubContract: " + e.getMessage());
        }
    }

    // NEW ENDPOINT: Get subcontracts by controller company ID
    @GetMapping("/controller-company/{controllerCompanyId}")
    public ResponseEntity<?> getSubContractsByControllerCompany(@PathVariable Long controllerCompanyId) {
        try {
            log.info("Fetching SubContracts for controller company id: {}", controllerCompanyId);
            List<SubContract> subContracts = subContractService.getSubContractsByControllerCompanyId(controllerCompanyId);
            return ResponseEntity.ok(subContracts);
        } catch (Exception e) {
            log.error("Error fetching SubContracts for controller company id: {}", controllerCompanyId, e);
            return ResponseEntity.status(500).body("Error fetching SubContracts: " + e.getMessage());
        }
    }

    // NEW ENDPOINT: Get subcontracts by contract ID
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<?> getSubContractsByContract(@PathVariable Long contractId) {
        try {
            log.info("Fetching SubContracts for contract id: {}", contractId);
            List<SubContract> subContracts = subContractService.getSubContractsByContractId(contractId);
            return ResponseEntity.ok(subContracts);
        } catch (Exception e) {
            log.error("Error fetching SubContracts for contract id: {}", contractId, e);
            return ResponseEntity.status(500).body("Error fetching SubContracts: " + e.getMessage());
        }
    }

    // NEW ENDPOINT: Get all subcontracts with company names
    @GetMapping("/with-names")
    public ResponseEntity<?> getAllSubContractsWithNames() {
        try {
            log.info("Fetching all SubContracts with company names");
            List<SubContractResponseDTO> subContracts = subContractService.getAllSubContractsWithNames();
            return ResponseEntity.ok(subContracts);
        } catch (Exception e) {
            log.error("Error fetching SubContracts with names", e);
            return ResponseEntity.status(500).body("Error fetching SubContracts: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubContract(@PathVariable Long id) {
        try {
            log.info("Deleting SubContract with id: {}", id);
            subContractService.deleteSubContract(id);
            return ResponseEntity.ok("SubContract deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting SubContract with id: {}", id, e);
            return ResponseEntity.status(500).body("Error deleting SubContract: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubContract(
            @PathVariable Long id,
            @RequestBody SubContractRequestDTO request) {
        try {
            log.info("Updating SubContract with id: {}, data: {}", id, request);
            subContractService.updateSubContract(id, request);
            return ResponseEntity.ok("SubContract updated successfully");
        } catch (Exception e) {
            log.error("Error updating SubContract with id: {}", id, e);
            return ResponseEntity.status(500).body("Error updating SubContract: " + e.getMessage());
        }
    }
}