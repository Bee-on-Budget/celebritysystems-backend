package com.celebritysystems.repository;

import com.celebritysystems.entity.SubContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubContractRepository extends JpaRepository<SubContract, Long> {
    
    List<SubContract> findByControllerCompanyId(Long controllerCompanyId);
    
    List<SubContract> findByContractId(Long contractId);
    
    @Query("SELECT sc FROM SubContract sc " +
           "JOIN FETCH sc.mainCompany " +
           "JOIN FETCH sc.controllerCompany " +
           "JOIN FETCH sc.contract")
    List<SubContract> findAllWithCompanyNames();
}