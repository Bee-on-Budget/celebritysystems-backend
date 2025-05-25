package com.celebritysystems.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.celebritysystems.entity.Company;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);
    
    Optional<Company> findByNameIgnoreCase(String name);
    
    List<Company> findByNameContainingIgnoreCase(String name);
    Optional<Company> findById(Long id);

    Optional<Company> getCompanyByName(String name);
    @Query(value = "SELECT COUNT(*) FROM company WHERE MONTH(created_at) = :month AND YEAR(created_at) = :year", nativeQuery = true)
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT YEAR(created_at) as year, MONTH(created_at) as month, COUNT(*) as total " +
            "FROM company GROUP BY year, month ORDER BY year, month", nativeQuery = true)
    List<Object[]> getMonthlyCompanyRegistrationStats();

    @Query(value = "SELECT YEAR(created_at) as year, COUNT(*) as total " +
            "FROM company GROUP BY year ORDER BY year", nativeQuery = true)
    List<Object[]> getAnnualCompanyRegistrationStats();
}