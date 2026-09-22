package br.com.fiap.prontus.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos do MVP (liberados temporariamente até o JWT)
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/api/patients/**").permitAll()
                        .requestMatchers("/api/triages/**").permitAll()
                        .requestMatchers("/api/queue/**").permitAll()
                        // Swagger / OpenAPI (para o vídeo de demonstração)
                        .requestMatchers(
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
