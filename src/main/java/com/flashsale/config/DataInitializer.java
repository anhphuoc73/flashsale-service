package com.flashsale.config;

import com.flashsale.user.entity.User;
import com.flashsale.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.role}")
    private String adminRole;

    @PostConstruct
    public void createAdmin() {

        if (userRepository.findByEmail(adminEmail).isEmpty()) {

            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPhone("0000000000");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setVerified(true);
            admin.setBalance(0.0);
            admin.setRole(adminRole);

            userRepository.save(admin);

            System.out.println("ADMIN account created!");
        }
    }
}