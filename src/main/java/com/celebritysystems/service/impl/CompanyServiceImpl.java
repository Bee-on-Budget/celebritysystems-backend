package com.celebritysystems.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.celebritysystems.dto.CompanyDTO;
import com.celebritysystems.entity.Company;
import com.celebritysystems.entity.User;
import com.celebritysystems.repository.CompanyRepository;
import com.celebritysystems.repository.UserRepository;
import com.celebritysystems.service.CompanyService;

import jakarta.transaction.Transactional;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyRepository companyRepository ;
    @Autowired 
    private UserRepository userRepository;
    
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
    public void assignUser(Long employeeId, Long companyId) {
       User user= userRepository.findById(employeeId).orElseThrow(()->new RuntimeException("User Not Found !"));
       Company company= companyRepository.findById(companyId).orElseThrow(()->new RuntimeException("User Not Found !"));

        user.setCompany(company);
        userRepository.save(user);
    }



    @Override
    @Transactional
    public Optional<Company> createCompany(CompanyDTO companyDto) {
   
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



  

    
}
