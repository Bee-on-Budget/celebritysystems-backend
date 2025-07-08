package com.celebritysystems.controller;

import com.celebritysystems.dto.*;
import com.celebritysystems.dto.subcontract.SubContractRequestDTO;
import com.celebritysystems.entity.SubContract;
import com.celebritysystems.entity.enums.SolutionTypeInScreen;
import com.celebritysystems.service.SubContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
            log.info("Received SubContract request");
            PaginatedResponse<SubContract> subContractList = subContractService.getSubContracts(page);
            return ResponseEntity.ok(subContractList);

        } catch (MultipartException e) {
            return ResponseEntity.badRequest().body("Invalid file upload");
        } catch (Exception e) {
            log.error("Error viewing SubContract", e);
            return ResponseEntity.status(500).body("Error viewing SubContract: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
public ResponseEntity<?> deleteSubContract(@PathVariable Long id) {
    try {
        subContractService.deleteSubContract(id);
        return ResponseEntity.ok("SubContract deleted successfully");
    } catch (Exception e) {
        log.error("Error deleting SubContract", e);
        return ResponseEntity.status(500).body("Error deleting SubContract: " + e.getMessage());
    }
}

}
