package br.com.fiap.prontus.auth.config;

import br.com.fiap.prontus.auth.model.Role;
import br.com.fiap.prontus.auth.model.User;
import br.com.fiap.prontus.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrap {

    @Bean
    public ApplicationRunner adminSeeder(UserRepository userRepository,
                                         PasswordEncoder passwordEncoder,
                                         @Value("${admin.username}") String username,
                                         @Value("${admin.password}") String password) {
        return args -> {
            if (!userRepository.existsByUsername(username)) {
                userRepository.save(new User(username, passwordEncoder.encode(password), Role.ADMIN));
            }
        };
    }
}
