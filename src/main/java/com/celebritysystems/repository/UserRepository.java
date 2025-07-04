package com.celebritysystems.repository;

import com.celebritysystems.entity.User;
import com.celebritysystems.entity.enums.RoleInSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
    Optional<List<User>> findAllByRole(RoleInSystem role);
}