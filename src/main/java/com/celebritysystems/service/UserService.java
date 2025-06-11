package com.celebritysystems.service;

import com.celebritysystems.entity.User;
import com.celebritysystems.entity.enums.RoleInSystem;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    void deleteById(Long id);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByUsername(String username);
    Optional<List<User>> getUsersByRole(RoleInSystem role);
    User updateUser(Long id, User user);
//    List<UserRegistrationStatsDTO> getUserRegistrationStats();
}