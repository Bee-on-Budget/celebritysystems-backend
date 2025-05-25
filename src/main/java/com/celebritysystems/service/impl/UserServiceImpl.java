package com.celebritysystems.service.impl;

import com.celebritysystems.dto.statistics.UserRegistrationStatsDTO;
import com.celebritysystems.entity.User;
import com.celebritysystems.repository.UserRepository;
import com.celebritysystems.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
// Create new user
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setCompany(user.getCompany());
        newUser.setRole(user.getRole());
        newUser.setCanEdit(user.getCanEdit());
        newUser.setCanRead(user.getCanRead());
        newUser.setFullName(user.getFullName());

        return userRepository.save(newUser);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserRegistrationStatsDTO> getUserRegistrationStats() {
        System.out.println("||||||||||||||||||||||||||||||  getUserRegistrationStats  ||||||||||||||||||||||||||||||||||||||");
//        return userRepository.getUserRegistrationStatsNative().stream()
//                .map(row -> new UserRegistrationStatsDTO(
//                        ((java.sql.Date) row[0]).toLocalDate(),
//                        ((Number) row[1]).longValue()
//                ))
//                .toList();
        return null;
    }


}