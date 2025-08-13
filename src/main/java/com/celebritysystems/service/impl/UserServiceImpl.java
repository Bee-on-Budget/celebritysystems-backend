package com.celebritysystems.service.impl;

import com.celebritysystems.entity.User;
import com.celebritysystems.entity.enums.RoleInSystem;
import com.celebritysystems.repository.UserRepository;
import com.celebritysystems.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public User updateUser(Long id, User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    // Update only the fields that should be editable
                    existingUser.setFullName(user.getFullName());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setRole(user.getRole());
                    existingUser.setCanEdit(user.getCanEdit());
                    existingUser.setCanRead(user.getCanRead());
                    existingUser.setPlayerId(user.getPlayerId());
                    // Note: We're not updating password or username here
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public User patchUser(Long id, Map<String, Object> updates) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    updates.forEach((key, value) -> {
                        switch (key) {
                            case "fullName":
                                existingUser.setFullName((String) value);
                                break;
                            case "email":
                                existingUser.setEmail((String) value);
                                break;
                            case "playerId":
                                existingUser.setPlayerId((String) value);
                                break;
                            case "role":
                                if (value instanceof String) {
                                    existingUser.setRole(RoleInSystem.valueOf((String) value));
                                }
                                break;
                            case "canEdit":
                                existingUser.setCanEdit((Boolean) value);
                                break;
                            case "canRead":
                                existingUser.setCanRead((Boolean) value);
                                break;
                            case "company":
                                // Handle company update if needed
                                break;
                            default:
                                // Ignore unknown fields
                                break;
                        }
                    });
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
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
        newUser.setPlayerId(user.getPlayerId());

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
    public Page<User> findAllPaginated(Pageable pageable, String search, RoleInSystem role, Long companyId) {
        return userRepository.findAll(createUserSpecification(search, role, companyId), pageable);
    }

    private Specification<User> createUserSpecification(String search, RoleInSystem role, Long companyId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search in username, email, or fullName
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), searchPattern));
                predicates.add(searchPredicate);
            }

            // Filter by role
            if (role != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }

            // Filter by company
            if (companyId != null) {
                predicates.add(criteriaBuilder.equal(root.get("company").get("id"), companyId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByPlayerId(String playerId) {
        return userRepository.findByPlayerId(playerId);
    }

    @Override
    public Optional<List<User>> getUsersByRole(RoleInSystem role) {
        return userRepository.findAllByRole(role);
    }

    @Override
    @Transactional
    public boolean resetPassword(Long userId, String newPassword) {

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    // @Override
    // public List<UserRegistrationStatsDTO> getUserRegistrationStats() {
    // System.out.println("|||||||||||||||||||||||||||||| getUserRegistrationStats
    // ||||||||||||||||||||||||||||||||||||||");
    //// return userRepository.getUserRegistrationStatsNative().stream()
    //// .map(row -> new UserRegistrationStatsDTO(
    //// ((java.sql.Date) row[0]).toLocalDate(),
    //// ((Number) row[1]).longValue()
    //// ))
    //// .toList();
    // return null;
    // }
}