package com.eventmanagement.service;

import com.eventmanagement.dto.request.AuthRequest;
import com.eventmanagement.dto.response.ApiResponses.AuthResponse;
import com.eventmanagement.dto.response.ApiResponses.UserResponse;

public interface AuthService {
    AuthResponse register(AuthRequest.Register request);
    AuthResponse login(AuthRequest.Login request);
    AuthResponse refreshToken(AuthRequest.RefreshToken request);
    UserResponse getCurrentUser(String email);
}
