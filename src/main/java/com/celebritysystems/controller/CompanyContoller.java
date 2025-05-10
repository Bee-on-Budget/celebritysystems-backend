package com.celebritysystems.controller;

import java.util.List;
import com.celebritysystems.service.impl.CompanyServiceImpl;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.celebritysystems.dto.CompanyDto;
import com.celebritysystems.entity.Company;
import com.celebritysystems.repository.CompanyRepository;


@RestController
@RequestMapping("/api/company")
public class CompanyContoller {

    private final CompanyServiceImpl companyServiceImpl;
    
    @Autowired
    private CompanyRepository companyRepository;


    CompanyContoller(CompanyServiceImpl companyServiceImpl) {
        this.companyServiceImpl = companyServiceImpl;
    }


     @GetMapping
    public ResponseEntity<List<Company>> getAllcompanies() {
        return ResponseEntity.ok(companyRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        return companyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Company> getCompanyByName(@PathVariable  String name) {
        return companyRepository.getCompanyByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<Company> createCompany(@RequestBody CompanyDto companyDto) {
        return companyServiceImpl.createCompany(companyDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        return companyRepository.findById(id)
                .map(company -> {
                    companyServiceImpl.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
