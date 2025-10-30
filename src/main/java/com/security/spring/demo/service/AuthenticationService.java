package com.security.spring.demo.service;

import com.security.spring.demo.dto.AuthenticationRequest;
import com.security.spring.demo.dto.AuthenticationResponse;
import com.security.spring.demo.dto.RegisterRequest;
import com.security.spring.demo.entity.Role;
import com.security.spring.demo.entity.User;
import com.security.spring.demo.repository.RoleRepository;
import com.security.spring.demo.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @Transactional
  public AuthenticationResponse register(final RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new RuntimeException("User with this email already exists");
    }
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new RuntimeException("User with this username already exists");
    }

    Role userRole =
        roleRepository
            .findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Default role not found"));

    Set<Role> roles = new HashSet<>();
    roles.add(userRole);

    var user =
        User.builder()
            .email(request.getEmail())
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .roles(roles)
            .enabled(true)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .credentialsNonExpired(true)
            .build();

    userRepository.save(user);

    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);

    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(3600L)
        .build();
  }

  @Transactional
  public AuthenticationResponse authenticate(final AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    var user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

    user.setLastLogin(LocalDateTime.now());
    userRepository.save(user);

    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);

    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(3600L)
        .build();
  }

  @Transactional(readOnly = true)
  public AuthenticationResponse refreshToken(final String refreshToken) {
    if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
      throw new RuntimeException("Invalid refresh token");
    }

    String token = refreshToken.substring(7);
    String userEmail = jwtService.extractUsername(token);

    if (userEmail != null) {
      var user =
          userRepository
              .findByEmail(userEmail)
              .orElseThrow(() -> new RuntimeException("User not found"));

      if (jwtService.isTokenValid(token, user)) {
        var accessToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(3600L)
            .build();
      }
    }
    throw new RuntimeException("Invalid refresh token");
  }
}
