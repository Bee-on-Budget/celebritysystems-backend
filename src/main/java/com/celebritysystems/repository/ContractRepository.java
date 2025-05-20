package com.celebritysystems.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.celebritysystems.entity.Contract;

public interface ContractRepository  extends JpaRepository<Contract, Long> {
    List<Contract> findByCompanyId(Long companyId);
    List<Contract> findByScreenId(Long screenId);
    boolean existsByCompanyIdAndScreenId(Long companyId, Long screenId);
    Optional<Contract> findFirstByScreenIdOrderByCreatedAtDesc(Long screenId);
    List<Contract> findByExpiredAtBetween(LocalDateTime startDate, LocalDateTime endDate);


}

