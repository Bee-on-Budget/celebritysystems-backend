package com.celebritysystems.controller;

import java.util.List;

import com.celebritysystems.dto.CompanyDto;
import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
import com.celebritysystems.service.CompanyService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import com.celebritysystems.entity.Company;
import com.celebritysystems.repository.CompanyRepository;


@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    
    private final CompanyRepository companyRepository;

     @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(companyRepository.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        return companyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Company> getCompanyByName(@PathVariable  String name) {
        return companyRepository.getCompanyByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<?> createCompany(@Valid @RequestBody CompanyDto companyDto) {
        try {
            Company company = companyService.createCompany(companyDto)
                .orElseThrow(() -> new RuntimeException("Failed to create company"));
            return ResponseEntity.ok(company);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        return companyRepository.findById(id)
                .map(company -> {
                    companyService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/ignore-case/{name}")
public ResponseEntity<Company> getCompanyByNameIgnoreCase(@PathVariable String name) {
    return companyService.findByNameIgnoreCase(name)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@GetMapping("/search/{name}")
public ResponseEntity<List<Company>> searchCompaniesByName(@PathVariable String name) {
    List<Company> companies = companyService.searchByName(name);
    return ResponseEntity.ok(companies);
}

    @GetMapping("/statistic/monthly")
    public List<MonthlyStats> getMonthlyStats() {
        return companyService.getMonthlyStats();
    }

    @GetMapping("/statistic/annual")
    public List<AnnualStats> getAnnualStats() {
        return companyService.getAnnualStats();
    }

    @GetMapping("/statistic/count")
    public long getCountByMonthAndYear(@RequestParam int month, @RequestParam int year) {
        return companyService.getCompanyCountByMonthAndYear(month, year);
    }
}
