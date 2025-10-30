package com.security.spring.demo.initializer;

import com.security.spring.demo.entity.Role;
import com.security.spring.demo.entity.User;
import com.security.spring.demo.repository.RoleRepository;
import com.security.spring.demo.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(String... args) {
    initializeRoles();
    initializeAdminUser();
  }

  private void initializeRoles() {
    if (roleRepository.count() == 0) {
      log.info("Initializing roles...");

      Role userRole = Role.builder().name("ROLE_USER").description("Default user role").build();

      Role adminRole = Role.builder().name("ROLE_ADMIN").description("Administrator role").build();

      Role moderatorRole =
          Role.builder().name("ROLE_MODERATOR").description("Moderator role").build();

      roleRepository.save(userRole);
      roleRepository.save(adminRole);
      roleRepository.save(moderatorRole);

      log.info("Roles initialized successfully");
    }
  }

  private void initializeAdminUser() {
    if (userRepository.count() == 0) {
      log.info("Creating default admin user...");

      Role adminRole =
          roleRepository
              .findByName("ROLE_ADMIN")
              .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

      Set<Role> roles = new HashSet<>();
      roles.add(adminRole);

      User admin =
          User.builder()
              .email("admin@example.com")
              .username("admin")
              .password(passwordEncoder.encode("Admin@123"))
              .firstName("Admin")
              .lastName("User")
              .roles(roles)
              .enabled(true)
              .accountNonExpired(true)
              .accountNonLocked(true)
              .credentialsNonExpired(true)
              .build();

      userRepository.save(admin);

      log.info("Default admin user created: admin@example.com / Admin@123");
    }
  }
}
