package com.celebritysystems.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.celebritysystems.entity.Contract;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByCompanyId(Long companyId);

    @Query("SELECT c FROM Contract c JOIN c.screenIds s WHERE s = :screenId")
    List<Contract> findByScreenId(@Param("screenId") Long screenId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contract c JOIN c.screenIds s WHERE s = :screenId AND c.companyId = :companyId")
    boolean existsByCompanyIdAndScreenId(@Param("companyId") Long companyId, @Param("screenId") Long screenId);

    @Query("SELECT c FROM Contract c JOIN c.screenIds s WHERE s = :screenId ORDER BY c.createdAt DESC")
    Optional<Contract> findFirstByScreenIdOrderByCreatedAtDesc(@Param("screenId") Long screenId);

    List<Contract> findByExpiredAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
