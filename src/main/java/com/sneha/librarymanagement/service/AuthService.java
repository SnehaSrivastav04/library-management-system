package com.sneha.librarymanagement.service;

import com.sneha.librarymanagement.dto.AuthResponse;
import com.sneha.librarymanagement.dto.LoginRequest;
import com.sneha.librarymanagement.dto.RegisterRequest;
import com.sneha.librarymanagement.dto.UserResponse;
import com.sneha.librarymanagement.entity.Role;
import com.sneha.librarymanagement.entity.User;
import com.sneha.librarymanagement.repository.UserRepository;
import com.sneha.librarymanagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .role(Role.MEMBER)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        UserDetails principal = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        return new AuthResponse(
                jwtService.generateToken(principal),
                "Bearer",
                jwtService.getExpirationMs(),
                UserResponse.from(user));
    }
}