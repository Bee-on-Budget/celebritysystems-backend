package com.celebritysystems.AuthControllers;

import com.celebritysystems.config.TokenProvider;
import com.celebritysystems.entity.User;
import com.celebritysystems.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
//        try {
//            if (userService.getUserByEmail(registrationDto.getEmail()).isPresent()) {
//                return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Email already in use");
//            }
//        //TODO
//        //    if (userService.getUserByUsername(registrationDto.getUsername()).isPresent()) {
//        //        return ResponseEntity
//        //            .status(HttpStatus.BAD_REQUEST)
//        //            .body("Username already taken");
//        //    }
//
//            // Create new user
//            User user = new User();
//            user.setUsername(registrationDto.getUsername());
//            user.setEmail(registrationDto.getEmail());
//            user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
//                try {
//                    RoleInSystem role = RoleInSystem.valueOf(registrationDto.getRole().toUpperCase());
//                    user.setRole(role);
//                } catch (IllegalArgumentException e) {
//                    return ResponseEntity.badRequest()
//                            .body("Invalid role: " + registrationDto.getRole().toUpperCase());
//                }
//
//
////            user.setRoles(validatedRoles);
//
//
//            User savedUser = userService.save(user);
//
//            // Generate token for immediate login after registration
//            String token = tokenProvider.generateToken(savedUser);
//
//            return ResponseEntity.ok(Collections.singletonMap("token", token));
//
//        } catch (Exception e) {
//            logger.error("Registration error", e);
//            return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Registration failed");
//        }
//    }



    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginCredentials) {
        try {
            String email = loginCredentials.get("email");
            String password = loginCredentials.get("password");

            Optional<User> optionalUser = userService.getUserByEmail(email);

            if (optionalUser.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
            }

            User user = optionalUser.get();
            
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
            }

            String token = tokenProvider.generateToken(user);
            return ResponseEntity.ok(Collections.singletonMap("token", token));
            
        } catch (Exception e) {
            logger.error("Login error", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Login failed");
        }
    }

}