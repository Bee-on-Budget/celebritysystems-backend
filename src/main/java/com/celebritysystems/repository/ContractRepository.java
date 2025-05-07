package com.celebritysystems.repository;

import com.celebritysystems.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByCompanyId(Long companyId);
    List<Contract> findByScreenId(Long screenId);
} 