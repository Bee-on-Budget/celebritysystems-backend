package com.celebritysystems.repository;

import com.celebritysystems.dto.statistics.UserRegistrationStatsDTO;
import com.celebritysystems.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

//    @Query(value = "SELECT DATE(created_at) AS date, COUNT(*) AS count " +
//            "FROM user GROUP BY DATE(created_at) ORDER BY DATE(created_at)",
//            nativeQuery = true)
//    List<Object[]> getUserRegistrationStatsNative();
}