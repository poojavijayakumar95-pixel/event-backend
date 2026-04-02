package com.eventmanagement.service;

import com.eventmanagement.dto.response.ApiResponses.DashboardStats;
import com.eventmanagement.dto.response.ApiResponses.UserResponse;

import java.util.List;

public interface AdminService {
    DashboardStats getDashboardStats();
    List<UserResponse> getAllUsers();
    void toggleUserStatus(Long userId);
    void promoteToAdmin(Long userId);
}
