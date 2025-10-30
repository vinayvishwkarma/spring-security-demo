package com.security.spring.demo.util;

import com.security.spring.demo.entity.User;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Utility class for Spring Security operations */
@Component
public class SecurityUtils {

  /** Get the current authenticated user */
  public static Optional<User> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication.getPrincipal().equals("anonymousUser")) {
      return Optional.empty();
    }

    if (authentication.getPrincipal() instanceof User) {
      return Optional.of((User) authentication.getPrincipal());
    }

    return Optional.empty();
  }

  /** Get the current authenticated username */
  public static Optional<String> getCurrentUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.empty();
    }

    return Optional.of(authentication.getName());
  }

  /** Check if the current user has a specific role */
  public static boolean hasRole(String role) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    return authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals(role));
  }

  /** Check if the current user is authenticated */
  public static boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null
        && authentication.isAuthenticated()
        && !authentication.getPrincipal().equals("anonymousUser");
  }
}
