package com.security.spring.demo.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.spring.demo.dto.AuthenticationRequest;
import com.security.spring.demo.dto.RegisterRequest;
import com.security.spring.demo.entity.Role;
import com.security.spring.demo.repository.RoleRepository;
import com.security.spring.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class SecurityIntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Autowired private RoleRepository roleRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();

    roleRepository.deleteAll();

    if (roleRepository.findByName("ROLE_USER").isEmpty()) {
      Role userRole = Role.builder().name("ROLE_USER").description("Default user role").build();
      roleRepository.save(userRole);
    }

    if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
      Role adminRole = Role.builder().name("ROLE_ADMIN").description("Administrator role").build();
      roleRepository.save(adminRole);
    }
  }

  @Test
  void publicEndpoint_shouldBeAccessibleWithoutAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/v1/public/hello"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void protectedEndpoint_shouldReturn401_whenNotAuthenticated() throws Exception {
    mockMvc.perform(get("/api/v1/user/profile")).andExpect(status().isUnauthorized());
  }

  @Test
  void register_shouldCreateNewUser() throws Exception {
    RegisterRequest request =
        RegisterRequest.builder()
            .email("test@example.com")
            .username("testuser")
            .password("Test@123456")
            .firstName("Test")
            .lastName("User")
            .build();

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists());
  }

  @Test
  void login_shouldReturnTokens_withValidCredentials() throws Exception {
    RegisterRequest registerRequest =
        RegisterRequest.builder()
            .email("login@example.com")
            .username("loginuser")
            .password("Login@123456")
            .firstName("Login")
            .lastName("User")
            .build();

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isOk());

    AuthenticationRequest loginRequest =
        AuthenticationRequest.builder().email("login@example.com").password("Login@123456").build();

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists());
  }

  @Test
  void login_shouldReturn401_withInvalidCredentials() throws Exception {
    AuthenticationRequest request =
        AuthenticationRequest.builder()
            .email("invalid@example.com")
            .password("wrongpassword")
            .build();

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void adminEndpoint_shouldReturn403_forRegularUser() throws Exception {
    RegisterRequest registerRequest =
        RegisterRequest.builder()
            .email("regular@example.com")
            .username("regularuser")
            .password("Regular@123456")
            .firstName("Regular")
            .lastName("User")
            .build();

    String response =
        mockMvc
            .perform(
                post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String token = objectMapper.readTree(response).get("accessToken").asText();

    mockMvc
        .perform(get("/api/v1/admin/dashboard").header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
  }
}
