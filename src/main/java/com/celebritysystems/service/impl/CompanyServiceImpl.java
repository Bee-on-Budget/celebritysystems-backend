package com.celebritysystems.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.celebritysystems.dto.CompanyDto;
import com.celebritysystems.entity.Company;
import com.celebritysystems.entity.User;
import com.celebritysystems.repository.CompanyRepository;
import com.celebritysystems.repository.UserRepository;
import com.celebritysystems.service.CompanyService;

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
    public Optional<Company> createCompany(CompanyDto companyDto) {
            Company company =new Company();
            if(companyDto.getName()!=null)
            company.setName(companyDto.getName());
            if(companyDto.getEmail()!=null)
            company.setEmail(companyDto.getEmail());
            if(companyDto.getPhone()!=null)
            company.setPhone(companyDto.getPhone());
            if(companyDto.getLocation()!=null)
            company.setLocation(companyDto.getLocation());
            
            return Optional.of(companyRepository.save(company));
        }





  

    
}
