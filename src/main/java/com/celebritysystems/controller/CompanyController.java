package com.celebritysystems.controller;

import com.celebritysystems.dto.CompanyDto;
import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
import com.celebritysystems.entity.Company;
import com.celebritysystems.repository.CompanyRepository;
import com.celebritysystems.service.CompanyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;

    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies() {
        log.info("Fetching all companies");
        return ResponseEntity.ok(companyRepository.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        log.info("Fetching company by ID: {}", id);
        return companyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Company with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Company> getCompanyByName(@PathVariable String name) {
        log.info("Fetching company by name: {}", name);
        return companyRepository.getCompanyByName(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Company with name '{}' not found", name);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<?> createCompany(@Valid @RequestBody CompanyDto companyDto) {
        log.info("Received request to create a new company");
        try {
            log.debug("Company creation payload: {}", companyDto);
            Company company = companyService.createCompany(companyDto)
                    .orElseThrow(() -> new RuntimeException("Failed to create company"));
            log.info("Successfully created company with ID: {}", company.getId());
            return ResponseEntity.ok(company);
        } catch (IllegalArgumentException e) {
            log.error("Validation error during company creation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("VALIDATION_ERROR", e.getMessage())
            );
        } catch (RuntimeException e) {
            log.error("Company creation failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("CREATION_FAILED", e.getMessage())
            );
        } catch (Exception e) {
            log.error("Unexpected error during company creation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("INTERNAL_SERVER_ERROR",
                            "An unexpected error occurred: " + e.getMessage())
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        log.info("Attempting to delete company with ID: {}", id);
        return companyRepository.findById(id)
                .map(company -> {
                    companyService.deleteById(id);
                    log.info("Successfully deleted company with ID: {}", id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> {
                    log.warn("Company with ID {} not found for deletion", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/name/ignore-case/{name}")
    public ResponseEntity<Company> getCompanyByNameIgnoreCase(@PathVariable String name) {
        log.info("Fetching company by name (ignore case): {}", name);
        return companyService.findByNameIgnoreCase(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Company with name '{}' not found (ignore case)", name);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<Company>> searchCompaniesByName(@PathVariable String name) {
        log.info("Searching for companies with name containing: {}", name);
        List<Company> companies = companyService.searchByName(name);
        log.info("Found {} companies matching: {}", companies.size(), name);
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/statistic/monthly")
    public List<MonthlyStats> getMonthlyStats() {
        log.info("Fetching monthly company statistics");
        return companyService.getMonthlyStats();
    }

    @GetMapping("/statistic/annual")
    public List<AnnualStats> getAnnualStats() {
        log.info("Fetching annual company statistics");
        return companyService.getAnnualStats();
    }

    @GetMapping("/statistic/count")
    public long getCountByMonthAndYear(@RequestParam int month, @RequestParam int year) {
        log.info("Getting count of companies for month {} and year {}", month, year);
        return companyService.getCompanyCountByMonthAndYear(month, year);
    }

    @GetMapping("statistic/total")
    public ResponseEntity<Long> getCompaniesCount() {
        Long count = companyService.getCompaniesCount();
        return ResponseEntity.ok(count);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ErrorResponse {
        private String errorCode;
        private String message;
    }
}