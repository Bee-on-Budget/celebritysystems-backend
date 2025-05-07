package com.celebritysystems.repository;

import com.celebritysystems.entity.CompanyRelations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRelationsRepository extends JpaRepository<CompanyRelations, Long> {
    List<CompanyRelations> findByCompanyMainId(Long companyMainId);
    List<CompanyRelations> findByCompanyAccessId(Long companyAccessId);
} 