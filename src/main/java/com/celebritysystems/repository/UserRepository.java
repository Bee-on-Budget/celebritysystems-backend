package com.celebritysystems.repository;

import com.celebritysystems.entity.User;
import com.celebritysystems.entity.enums.RoleInSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByPlayerId(String playerId);
    Optional<List<User>> findAllByRole(RoleInSystem role);
    List<User> findByCompanyIdAndPlayerIdIsNotNull(Long companyId);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByPlayerId(String playerId);
    List<User> findByCompanyId(Long companyId);

    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count " +
                   "FROM users " +
                   "WHERE created_at BETWEEN :startDate AND :endDate " +
                   "GROUP BY DATE(created_at) " +
                   "ORDER BY date", nativeQuery = true)
    List<Object[]> getDailyUserCreationStats(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

}
