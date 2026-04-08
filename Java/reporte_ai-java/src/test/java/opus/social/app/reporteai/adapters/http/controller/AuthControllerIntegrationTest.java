package opus.social.app.reporteai.adapters.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import opus.social.app.reporteai.application.dto.RegisterRequest;
import opus.social.app.reporteai.application.service.AuthUserApplicationService;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthRoleJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthUserJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração para AuthController
 */
@SpringBootTest(properties = {"spring.flyway.validate-on-migrate=false"})
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthUserApplicationService authUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
            "integrationuser" + System.currentTimeMillis(),
            "integ" + System.currentTimeMillis() + "@example.com",
            "testPassword123",
            "testPassword123",
            "Integration Test User"
        );
    }

    @Test
    void testHealthCheckEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/auth/health")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testRegisterUserSuccess() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.username").value(registerRequest.getUsername()))
            .andExpect(jsonPath("$.email").value(registerRequest.getEmail()))
            .andExpect(jsonPath("$.userId").exists())
            .andReturn();

        assertThat(result.getResponse().getContentAsString()).isNotEmpty();
    }

    @Test
    void testRegisterUserWithMismatchedPasswords() throws Exception {
        registerRequest.setPasswordConfirm("differentPassword");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("PASSWORDS_DONT_MATCH"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        // First register a user
        AuthUserJpaEntity newUser = authUserService.registerUser(registerRequest);

        // Then attempt to login
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest(
            registerRequest.getUsername(),
            registerRequest.getPassword()
        );

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").value(86400))
            .andExpect(jsonPath("$.username").value(registerRequest.getUsername()));
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest(
            "nonexistentuser",
            "wrongpassword"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("AUTHENTICATION_FAILED"))
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testLoginWithoutPasswordField() throws Exception {
        String invalidJson = "{\"username\":\"testuser\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testValidateTokenWithValidToken() throws Exception {
        // First register and login
        authUserService.registerUser(registerRequest);
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest(
            registerRequest.getUsername(),
            registerRequest.getPassword()
        );

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String accessToken = objectMapper.readTree(responseBody).get("accessToken").asText();

        // Now validate the token
        AuthController.ValidateTokenRequest validateRequest = new AuthController.ValidateTokenRequest(accessToken);

        mockMvc.perform(post("/api/v1/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isValid").value(true))
            .andExpect(jsonPath("$.username").value(registerRequest.getUsername()))
            .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    void testValidateTokenWithInvalidToken() throws Exception {
        AuthController.ValidateTokenRequest validateRequest = 
            new AuthController.ValidateTokenRequest("invalidtoken123");

        mockMvc.perform(post("/api/v1/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isValid").value(false));
    }

    @Test
    void testRefreshToken() throws Exception {
        // First register and login
        authUserService.registerUser(registerRequest);
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest(
            registerRequest.getUsername(),
            registerRequest.getPassword()
        );

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(responseBody).get("refreshToken").asText();

        // Now refresh the token
        AuthController.RefreshTokenRequest refreshRequest = 
            new AuthController.RefreshTokenRequest(refreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").value(86400));
    }

    @Test
    void testRefreshTokenWithInvalidToken() throws Exception {
        AuthController.RefreshTokenRequest refreshRequest = 
            new AuthController.RefreshTokenRequest("invalidrefreshtoken");

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("INVALID_REFRESH_TOKEN"));
    }

    @Test
    void testRegisterUserWithDuplicateUsername() throws Exception {
        // Register first user
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated());

        // Try to register with same username
        RegisterRequest duplicateRequest = new RegisterRequest(
            registerRequest.getUsername(),
            "different@example.com",
            "password123",
            "password123",
            "Different User"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("REGISTRATION_FAILED"));
    }

    @Test
    void testRegisterUserWithDuplicateEmail() throws Exception {
        // Register first user
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated());

        // Try to register with same email
        RegisterRequest duplicateRequest = new RegisterRequest(
            "differentuser",
            registerRequest.getEmail(),
            "password123",
            "password123",
            "Different User"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("REGISTRATION_FAILED"));
    }
}
