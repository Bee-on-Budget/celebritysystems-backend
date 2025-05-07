package com.celebritysystems.service;

import com.celebritysystems.entity.Company;
import java.util.List;
import java.util.Optional;

public interface CompanyService {
    List<Company> findAll();
    Optional<Company> findById(Long id);
    Company save(Company company);
    void deleteById(Long id);
} 