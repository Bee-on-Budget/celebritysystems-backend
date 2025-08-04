package com.celebritysystems.service.impl;

import java.util.List;
import java.util.Optional;

import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.celebritysystems.dto.CompanyDto;
import com.celebritysystems.entity.Company;
import com.celebritysystems.entity.User;
import com.celebritysystems.repository.CompanyRepository;
import com.celebritysystems.repository.UserRepository;
import com.celebritysystems.service.CompanyService;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Override
    public List<Company> findAll() {
        return companyRepository.findAll();
    }


    @Override
    public Optional<Company> findByName(String Name) {
        return companyRepository.findByName(Name);
    }

    @Override
    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }

    @Override
    public long getCompanyCountByMonthAndYear(int month, int year) {
        return companyRepository.countByMonthAndYear(month, year);
    }

    @Override
    public List<MonthlyStats> getMonthlyStats() {
        return companyRepository.getMonthlyCompanyRegistrationStats()
                .stream()
                .map(record -> new MonthlyStats(
                        ((Number) record[0]).intValue(),
                        ((Number) record[1]).intValue(),
                        ((Number) record[2]).longValue()))
                .toList();
    }

    @Override
    public List<AnnualStats> getAnnualStats() {
        return companyRepository.getAnnualCompanyRegistrationStats()
                .stream()
                .map(record -> new AnnualStats(
                        ((Number) record[0]).intValue(),
                        ((Number) record[1]).longValue()))
                .toList();
    }

    @Override
    public Long getCompaniesCount() {
        return companyRepository.count();
    }

    @Override
    public Page<Company> findAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return companyRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public CompanyDto updateCompany(Long id, CompanyDto companyDto) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company with ID " + id + " not found"));

        // Check if the email is being changed and if it already exists
        if (companyDto.getName() != null) {
            existingCompany.setName(companyDto.getName());
        }
        if (companyDto.getPhone() != null) {
            existingCompany.setPhone(companyDto.getPhone());
        }
        if (companyDto.getEmail() != null) {
            if (!existingCompany.getEmail().equals(companyDto.getEmail()) &&
                    companyRepository.existsByEmail(companyDto.getEmail())) {
                throw new IllegalArgumentException("Email '" + companyDto.getEmail() + "' is already in use");
            }
            existingCompany.setEmail(companyDto.getEmail());
        }
        if (companyDto.getLocation() != null) {
            existingCompany.setLocation(companyDto.getLocation());
        }

        Company updatedCompany = companyRepository.save(existingCompany);

        // Convert back to DTO
        return CompanyDto.builder()
                .name(updatedCompany.getName())
                .phone(updatedCompany.getPhone())
                .email(updatedCompany.getEmail())
                .location(updatedCompany.getLocation())
                .build();
    }


    @Override
    public void assignUser(Long employeeId, Long companyId) {
        User user = userRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("User Not Found !"));
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new RuntimeException("User Not Found !"));

        user.setCompany(company);
        userRepository.save(user);
    }


    @Override
    @Transactional
    public Optional<Company> createCompany(CompanyDto companyDto) {

        Company company = Company.builder()
                .name(companyDto.getName())
                .phone(companyDto.getPhone())
                .email(companyDto.getEmail())
                .location(companyDto.getLocation())
                .activated(true)
                .companyType("DEFAULT")
                .build();

        return Optional.of(companyRepository.save(company));
    }


    @Override
    public Optional<Company> findByNameIgnoreCase(String name) {
        return companyRepository.findByNameIgnoreCase(name);
    }

    @Override
    public List<Company> searchByName(String name) {
        return companyRepository.findByNameContainingIgnoreCase(name);
    }


}
