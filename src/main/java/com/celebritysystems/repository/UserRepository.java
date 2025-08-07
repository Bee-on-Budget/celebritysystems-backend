package com.celebritysystems.repository;

import com.celebritysystems.entity.User;
import com.celebritysystems.entity.enums.RoleInSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByPlayerId(String playerId);

    Optional<List<User>> findAllByRole(RoleInSystem role);

    List<User> findByCompanyIdAndPlayerIdIsNotNull(Long companyId);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPlayerId(String playerId);
}