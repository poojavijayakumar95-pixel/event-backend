package com.eventmanagement.service;

import com.eventmanagement.dto.request.AuthRequest;
import com.eventmanagement.dto.response.ApiResponses.AuthResponse;
import com.eventmanagement.entity.User;
import com.eventmanagement.enums.Role;
import com.eventmanagement.exception.AppExceptions;
import com.eventmanagement.repository.UserRepository;
import com.eventmanagement.security.JwtUtils;
import com.eventmanagement.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtils jwtUtils;
    @Mock private UserDetailsService userDetailsService;
    @Mock private EmailService emailService;

    @InjectMocks private AuthServiceImpl authService;

    private User sampleUser;
    private UserDetails sampleUserDetails;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L).firstName("John").lastName("Doe")
                .email("john@example.com").password("encoded")
                .role(Role.ROLE_USER).enabled(true).build();

        sampleUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username("john@example.com").password("encoded")
                .authorities(List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }

    @Test
    @DisplayName("Register - success")
    void register_success() {
        AuthRequest.Register req = new AuthRequest.Register();
        req.setFirstName("John"); req.setLastName("Doe");
        req.setEmail("john@example.com"); req.setPassword("Password1");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(sampleUserDetails);
        when(jwtUtils.generateAccessToken(any())).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(any())).thenReturn("refresh-token");
        doNothing().when(emailService).sendWelcomeEmail(any());

        AuthResponse result = authService.register(req);

        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getUser().getEmail()).isEqualTo("john@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Register - duplicate email throws DuplicateResourceException")
    void register_duplicateEmail_throws() {
        AuthRequest.Register req = new AuthRequest.Register();
        req.setEmail("john@example.com");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(AppExceptions.DuplicateResourceException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    @DisplayName("Login - success")
    void login_success() {
        AuthRequest.Login req = new AuthRequest.Login();
        req.setEmail("john@example.com"); req.setPassword("Password1");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(sampleUserDetails);
        when(jwtUtils.generateAccessToken(any())).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(any())).thenReturn("refresh-token");

        AuthResponse result = authService.login(req);

        assertThat(result.getAccessToken()).isEqualTo("access-token");
    }

    @Test
    @DisplayName("Login - bad credentials throws")
    void login_badCredentials_throws() {
        AuthRequest.Login req = new AuthRequest.Login();
        req.setEmail("john@example.com"); req.setPassword("wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class);
    }
}
