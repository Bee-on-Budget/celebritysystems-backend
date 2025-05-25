package com.celebritysystems.controller;

import com.celebritysystems.AuthControllers.AuthController;
import com.celebritysystems.config.TokenProvider;
import com.celebritysystems.dto.statistics.UserRegistrationStatsDTO;
import com.celebritysystems.entity.User;
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
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {

        System.out.println(userRepository.findByUsername("sami"));
        return userService.getUserByUsername(username).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")  
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        System.out.println("helllllllllllllllllllllllllllllllllllllllllo");
        try {
            if (userService.getUserByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
            }
            if (userService.getUserByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already taken");
            }

            User companyUser = userService.save(user);


            // Generate token for immediate login after registration
            String token = tokenProvider.generateToken(companyUser);

            return ResponseEntity.ok(Collections.singletonMap("token", token));

        } catch (Exception e) {
            logger.error("Registration error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }


//        return ResponseEntity.ok(companyUser);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return userService.findById(id).map(user -> {
            userService.deleteById(id);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("static/registrations")
    public List<UserRegistrationStatsDTO> getUserRegistrationStats() {
        return userService.getUserRegistrationStats();
    }
} 