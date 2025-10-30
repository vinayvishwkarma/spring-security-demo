package com.security.spring.demo.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DemoController {

  @GetMapping("/public/hello")
  public ResponseEntity<Map<String, String>> publicHello() {
    Map<String, String> response = new HashMap<>();
    response.put("message", "This is a public endpoint");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/user/profile")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<Map<String, Object>> getUserProfile(
      final @AuthenticationPrincipal UserDetails userDetails) {
    Map<String, Object> response = new HashMap<>();
    response.put("username", userDetails.getUsername());
    response.put("authorities", userDetails.getAuthorities());
    response.put("message", "This is a protected user endpoint");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/admin/dashboard")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> getAdminDashboard(
      final Authentication authentication) {
    Map<String, String> response = new HashMap<>();
    response.put("message", "Welcome to admin dashboard");
    response.put("user", authentication.getName());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/admin/users")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> createUser() {
    Map<String, String> response = new HashMap<>();
    response.put("message", "User created successfully");
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/admin/users/{id}")
  @PreAuthorize("hasRole('ADMIN') and hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<Map<String, String>> deleteUser(final @PathVariable Long id) {
    Map<String, String> response = new HashMap<>();
    response.put("message", "User deleted successfully");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/moderator/content")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  public ResponseEntity<Map<String, String>> moderateContent() {
    Map<String, String> response = new HashMap<>();
    response.put("message", "Content moderation dashboard");
    return ResponseEntity.ok(response);
  }
}
