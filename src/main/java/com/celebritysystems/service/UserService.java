package com.celebritysystems.service;

import com.celebritysystems.dto.UserResponseDTO;
import com.celebritysystems.entity.User;
import com.celebritysystems.entity.enums.RoleInSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    void deleteById(Long id);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByPlayerId(String playerId);

    Optional<List<User>> getUsersByRole(RoleInSystem role);

    User updateUser(Long id, User user);

    User patchUser(Long id, Map<String, Object> updates);

    boolean resetPassword(Long userId, String newPassword);

    Page<User> findAllPaginated(Pageable pageable, String search, RoleInSystem role, Long companyId);

    Page<UserResponseDTO> findAllPaginatedAsDTO(Pageable pageable, String search, RoleInSystem role, Long companyId);
    
    // Add this new method
    void assignUserToCompany(Long userId, Long companyId);
}