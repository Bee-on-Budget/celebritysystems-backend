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

        List<Contract> findByExpiredAtBetween(LocalDateTime startDate, LocalDateTime endDate);

        @Query(value = "SELECT COUNT(*) FROM contract WHERE MONTH(created_at) = :month AND YEAR(created_at) = :year", nativeQuery = true)
        long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

        @Query(value = "SELECT YEAR(created_at) as year, MONTH(created_at) as month, COUNT(*) as total " +
                        "FROM contract GROUP BY year, month ORDER BY year, month", nativeQuery = true)
        List<Object[]> getMonthlyContractRegistrationStats();

        @Query("SELECT c FROM Contract c JOIN Company comp ON c.companyId = comp.id WHERE LOWER(comp.name) LIKE LOWER(CONCAT('%', :companyName, '%'))")
        List<Contract> findByCompanyNameContainingIgnoreCase(@Param("companyName") String companyName);

        @Query("SELECT SUM(c.contractValue) FROM Contract c")
        Double sumAllContractValues();

        @Query(value = "SELECT YEAR(created_at) as year, COUNT(*) as total " +
                        "FROM contract GROUP BY year ORDER BY year", nativeQuery = true)
        List<Object[]> getAnnualContractRegistrationStats();


         @Query("SELECT DISTINCT screenId FROM Contract c JOIN c.screenIds screenId WHERE c.expiredAt > CURRENT_TIMESTAMP")
        List<Long> findActiveContractScreenIds();
        @Query("SELECT c FROM Contract c WHERE c.expiredAt > :currentTime")
        List<Contract> findActiveContracts(@Param("currentTime") LocalDateTime currentTime);
}