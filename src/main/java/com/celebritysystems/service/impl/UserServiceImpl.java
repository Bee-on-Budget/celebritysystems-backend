package com.celebritysystems.service.impl;

import com.celebritysystems.dto.UserResponseDTO;
import com.celebritysystems.entity.Company;
import com.celebritysystems.entity.User;
import com.celebritysystems.entity.enums.RoleInSystem;
import com.celebritysystems.repository.CompanyRepository;
import com.celebritysystems.repository.UserRepository;
import com.celebritysystems.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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

                    // Handle company assignment if provided
                    if (user.getCompany() != null && user.getCompany().getId() != null) {
                        Company company = companyRepository.findById(user.getCompany().getId())
                                .orElseThrow(() -> new RuntimeException(
                                        "Company not found with id: " + user.getCompany().getId()));
                        existingUser.setCompany(company);
                    }

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
                            case "companyId":
                                if (value != null) {
                                    Long parsedCompanyId;
                                    if (value instanceof Number) {
                                        parsedCompanyId = ((Number) value).longValue();
                                    } else if (value instanceof String) {
                                        try {
                                            parsedCompanyId = Long.parseLong((String) value);
                                        } catch (NumberFormatException e) {
                                            logger.warn("Invalid companyId format: {}", value);
                                            break;
                                        }
                                    } else {
                                        // Unsupported type
                                        break;
                                    }

                                    Company company = companyRepository.findById(parsedCompanyId)
                                            .orElseThrow(() -> new RuntimeException(
                                                    "Company not found with id: " + parsedCompanyId));
                                    existingUser.setCompany(company);

                                } else {
                                    existingUser.setCompany(null);
                                }

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
        newUser.setRole(user.getRole());
        newUser.setCanEdit(user.getCanEdit());
        newUser.setCanRead(user.getCanRead());
        newUser.setFullName(user.getFullName());
        newUser.setPlayerId(user.getPlayerId());

        // Handle company assignment if provided
        if (user.getCompany() != null && user.getCompany().getId() != null) {
            Company company = companyRepository.findById(user.getCompany().getId())
                    .orElseThrow(() -> new RuntimeException("Company not found with id: " + user.getCompany().getId()));
            newUser.setCompany(company);
        }

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

    @Override
    public Page<UserResponseDTO> findAllPaginatedAsDTO(Pageable pageable, String search, RoleInSystem role,
            Long companyId) {
        // Use the specification to get users with eager loading for company
        Page<User> userPage = userRepository.findAll(createUserSpecificationWithJoin(search, role, companyId),
                pageable);
        return userPage.map(this::convertToUserResponseDTO);
    }

    private UserResponseDTO convertToUserResponseDTO(User user) {
        logger.debug("Converting user: {} with company: {}",
                user.getUsername(),
                user.getCompany() != null ? user.getCompany().getName() : "null");

        UserResponseDTO.UserResponseDTOBuilder builder = UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .playerId(user.getPlayerId())
                .role(user.getRole())
                .canRead(user.getCanRead())
                .canEdit(user.getCanEdit())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt());

        // Handle company information with null safety
        if (user.getCompany() != null) {
            Company company = user.getCompany();
            builder.companyId(company.getId())
                    .companyName(company.getName())
                    .companyType(company.getCompanyType())
                    .companyEmail(company.getEmail())
                    .companyLocation(company.getLocation());
        } else {
            // Explicitly set null values
            builder.companyId(null)
                    .companyName(null)
                    .companyType(null)
                    .companyEmail(null)
                    .companyLocation(null);
        }

        return builder.build();
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

    private Specification<User> createUserSpecificationWithJoin(String search, RoleInSystem role, Long companyId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add LEFT JOIN FETCH for company to avoid N+1 queries
            if (query != null) {
                query.distinct(true);
                root.fetch("company", JoinType.LEFT);
            }

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
                Join<User, Company> companyJoin = root.join("company", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(companyJoin.get("id"), companyId));
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

    @Override
    @Transactional
    public void assignUserToCompany(Long userId, Long companyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        user.setCompany(company);
        userRepository.save(user);
        logger.info("Assigned user {} to company {}", user.getUsername(), company.getName());
    }
}