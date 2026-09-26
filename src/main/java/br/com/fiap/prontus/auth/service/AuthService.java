package br.com.fiap.prontus.auth.service;

import br.com.fiap.prontus.auth.dto.AuthDto;
import br.com.fiap.prontus.auth.model.Role;
import br.com.fiap.prontus.auth.model.User;
import br.com.fiap.prontus.auth.repository.UserRepository;
import br.com.fiap.prontus.shared.exception.ConflictException;
import br.com.fiap.prontus.shared.exception.UnauthorizedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) throws ConflictException {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already taken: " + request.username());
        }
        Role role = request.role() != null ? request.role() : Role.PATIENT;
        User user = new User(request.username(), passwordEncoder.encode(request.password()), role);
        userRepository.save(user);
        return new AuthDto.AuthResponse(jwtService.generateToken(user.getUsername(), user.getRole().name()),
                "Bearer", user.getRole().name());
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) throws UnauthorizedException {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        return new AuthDto.AuthResponse(jwtService.generateToken(user.getUsername(), user.getRole().name()),
                "Bearer", user.getRole().name());
    }
}
