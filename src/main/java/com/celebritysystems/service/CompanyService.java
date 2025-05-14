package com.celebritysystems.service;

import java.util.List;
import java.util.Optional;

import com.celebritysystems.dto.CompanyDto;
import com.celebritysystems.entity.Company;

public interface CompanyService {
List<Company> findAll();
Optional<Company> findById(Long id);
Optional<Company> findByName(String Name);
void assignUser(Long employeeId, Long companyId);
Optional<Company> createCompany(CompanyDto companyDto);
void deleteById(Long id);
}
