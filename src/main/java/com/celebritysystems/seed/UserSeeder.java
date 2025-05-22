package com.celebritysystems.seed;

import com.celebritysystems.entity.User;
import com.celebritysystems.entity.enums.RoleInSystem;
import com.celebritysystems.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

// this class works on application run
// it fills the table Role with roles/
@Component
public class UserSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadUsers();
    }

    private void loadUsers() {


        Optional<User> optionalEmailUser = userRepository.findByEmail("admin@gmail.com");
        if (optionalEmailUser.isPresent()) {
            System.out.println("Admin account already have been added");
            return;
        } else {
            User userAdmin = new User();
            userAdmin.setEmail("admin@gmail.com");
            userAdmin.setUsername("admin");
            userAdmin.setFullName("Admin");
            userAdmin.setRole(RoleInSystem.ADMIN);
            userAdmin.setPassword(passwordEncoder.encode("123"));
            userAdmin.setCanRead(true);
            userAdmin.setCanEdit(true);

            userRepository.save(userAdmin);
            System.out.println("User with name " + userAdmin.getUsername() + " have been saved");
        }


    }
}
