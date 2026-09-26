package br.com.fiap.prontus.auth.config;

import br.com.fiap.prontus.auth.model.Role;
import br.com.fiap.prontus.auth.model.User;
import br.com.fiap.prontus.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        return args -> {
            createUserIfMissing(userRepository, passwordEncoder, "admin", "admin-prontus-2026", Role.ADMIN);
            createUserIfMissing(userRepository, passwordEncoder, "nurse", "123456", Role.NURSE);
            createUserIfMissing(userRepository, passwordEncoder, "doctor", "123456", Role.DOCTOR);
            log.info("Seed data check completed");
        };
    }

    private void createUserIfMissing(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     String username,
                                     String rawPassword,
                                     Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        userRepository.save(user);
        log.info("Seeded user: {} ({})", username, role);
    }
}
