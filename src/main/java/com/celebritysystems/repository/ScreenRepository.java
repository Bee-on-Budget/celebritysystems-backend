package com.celebritysystems.repository;

import com.celebritysystems.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen, Long> {
        @Query(value = "SELECT COUNT(*) FROM screen WHERE MONTH(created_at) = :month AND YEAR(created_at) = :year", nativeQuery = true)
        long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

      
        @Query("SELECT s FROM Screen s WHERE s.id NOT IN " +
                        "(SELECT DISTINCT sc FROM Contract c JOIN c.screenIds sc WHERE c.expiredAt > CURRENT_TIMESTAMP)")
        List<Screen> findScreensWithoutActiveContracts();

        @Query(value = "SELECT YEAR(created_at) as year, MONTH(created_at) as month, COUNT(*) as total " +
                        "FROM screen GROUP BY year, month ORDER BY year, month", nativeQuery = true)
        List<Object[]> getMonthlyScreenRegistrationStats();

        @Query(value = "SELECT YEAR(created_at) as year, COUNT(*) as total " +
                        "FROM screen GROUP BY year ORDER BY year", nativeQuery = true)
        List<Object[]> getAnnualScreenRegistrationStats();

        List<Screen> findByNameContainingIgnoreCase(String name);
}
