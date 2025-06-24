package com.celebritysystems.service;

import java.util.List;
import java.util.Optional;

import com.celebritysystems.dto.CompanyDto;
import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
import com.celebritysystems.entity.Company;

public interface CompanyService {
    List<Company> findAll();

    Optional<Company> findById(Long id);

    Optional<Company> findByName(String Name);

    Optional<Company> findByNameIgnoreCase(String name);

    List<Company> searchByName(String name);

    void assignUser(Long employeeId, Long companyId);

    Optional<Company> createCompany(CompanyDto companyDto);

    void deleteById(Long id);
    long getCompanyCountByMonthAndYear(int month, int year);
    List<MonthlyStats> getMonthlyStats();
    List<AnnualStats> getAnnualStats();
    Long getCompaniesCount();
}
