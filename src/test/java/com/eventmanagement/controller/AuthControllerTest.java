package com.eventmanagement.controller;

import com.eventmanagement.dto.request.AuthRequest;
import com.eventmanagement.dto.response.ApiResponses.AuthResponse;
import com.eventmanagement.service.AuthService;
// Import your JwtUtils and JwtAuthenticationFilter here
import com.eventmanagement.security.JwtUtils;
import com.eventmanagement.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthService authService;

    // FIX: Mock the missing security beans to satisfy the Application Context
    @MockBean private JwtUtils jwtUtils;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("POST /auth/register - 201 on valid request")
    void register_validRequest_returns201() throws Exception {
        AuthRequest.Register req = new AuthRequest.Register();
        req.setFirstName("Jane"); req.setLastName("Doe");
        req.setEmail("jane@example.com"); req.setPassword("Password1");

        AuthResponse mockResp = AuthResponse.builder()
                .accessToken("token").refreshToken("refresh").tokenType("Bearer").build();

        when(authService.register(any())).thenReturn(mockResp);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("token"));
    }

    @Test
    @DisplayName("POST /auth/register - 400 on invalid email")
    void register_invalidEmail_returns400() throws Exception {
        AuthRequest.Register req = new AuthRequest.Register();
        req.setFirstName("Jane"); req.setLastName("Doe");
        req.setEmail("not-an-email"); req.setPassword("Password1");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login - 200 on valid credentials")
    void login_validCredentials_returns200() throws Exception {
        AuthRequest.Login req = new AuthRequest.Login();
        req.setEmail("jane@example.com"); req.setPassword("Password1");

        AuthResponse mockResp = AuthResponse.builder()
                .accessToken("token").refreshToken("refresh").tokenType("Bearer").build();

        when(authService.login(any())).thenReturn(mockResp);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }
}