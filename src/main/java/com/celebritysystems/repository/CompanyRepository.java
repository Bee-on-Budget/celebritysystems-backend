package com.celebritysystems.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.celebritysystems.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);
    
    Optional<Company> findByNameIgnoreCase(String name);
    
    List<Company> findByNameContainingIgnoreCase(String name);
    Optional<Company> findById(Long id);

    Optional<Company> getCompanyByName(String name);
}