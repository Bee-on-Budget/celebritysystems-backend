package com.celebritysystems.controller;

import com.celebritysystems.AuthControllers.AuthController;
import com.celebritysystems.config.TokenProvider;
import com.celebritysystems.entity.User;
import com.celebritysystems.entity.enums.RoleInSystem;
import com.celebritysystems.repository.UserRepository;
import com.celebritysystems.service.UserService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenProvider tokenProvider;

    private final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userService.findAll();
        logger.debug("Found {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Fetching user by id: {}", id);
        return userService.findById(id)
                .map(user -> {
                    logger.debug("User found: {}", user.getUsername());
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    logger.warn("User not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/id/{id}/reset-password")
    public ResponseEntity<?> resetPasswordById(@PathVariable Long id, @RequestBody Map<String, String> request) {
        logger.info("Attempt to reset password for user with id: {}", id);

        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            logger.warn("Invalid password provided for user id: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password is required");
        }

        try {
            boolean success = userService.resetPassword(id, newPassword);
            if (success) {
                logger.info("Password reset successfully for user with id: {}", id);
                return ResponseEntity.ok(Collections.singletonMap("message", "Password reset successfully"));
            } else {
                logger.warn("User not found for password reset with id: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            logger.error("Error resetting password for user id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resetting password");
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        logger.info("Fetching user by username: {}", username);
        logger.debug("Debug: Testing repository call for 'sami': {}", userRepository.findByUsername("sami"));
        return userService.getUserByUsername(username)
                .map(user -> {
                    logger.debug("User found: {}", user.getUsername());
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    logger.warn("User not found with username: {}", username);
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        logger.info("Attempt to create user with username: {} and email: {}", user.getUsername(), user.getEmail());
        try {
            if (userService.getUserByEmail(user.getEmail()).isPresent()) {
                logger.warn("Email already in use: {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
            }
            if (userService.getUserByUsername(user.getUsername()).isPresent()) {
                logger.warn("Username already taken: {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already taken");
            }

            User companyUser = userService.save(user);
            logger.info("User created successfully with username: {}", companyUser.getUsername());

            // Generate token for immediate login after registration
            String token = tokenProvider.generateToken(companyUser);
            logger.debug("Generated token for user: {}", companyUser.getUsername());

            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (Exception e) {
            logger.error("Registration error for user: {}", user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        logger.info("Attempt to update user with id: {}", id);
        try {
            User updatedUser = userService.updateUser(id, user);
            logger.info("User updated successfully with id: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            logger.error("Error updating user with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error updating user with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Request to delete user with id: {}", id);
        return userService.findById(id)
                .map(user -> {
                    userService.deleteById(id);
                    logger.info("User deleted with id: {}", id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("User not found for deletion with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/roles/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable RoleInSystem role) {
        logger.info("Fetching users with role: {}", role);
        return userService.getUsersByRole(role)
                .map(users -> {
                    logger.debug("Found {} users with role {}", users.size(), role);
                    return ResponseEntity.ok(users);
                })
                .orElseGet(() -> {
                    logger.warn("No users found with role: {}", role);
                    return ResponseEntity.notFound().build();
                });
    }

}
