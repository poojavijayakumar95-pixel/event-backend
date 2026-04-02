package com.eventmanagement.controller;

import com.eventmanagement.dto.request.UpdateAttendanceRequest;
import com.eventmanagement.dto.response.ApiResponses.*;
import com.eventmanagement.service.AdminService;
import com.eventmanagement.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin-only management endpoints")
public class AdminController {

    private final AdminService adminService;
    private final RegistrationService registrationService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<DashboardStats> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{userId}/toggle-status")
    @Operation(summary = "Enable/disable a user account")
    public ResponseEntity<MessageResponse> toggleUserStatus(@PathVariable Long userId) {
        adminService.toggleUserStatus(userId);
        return ResponseEntity.ok(MessageResponse.builder().message("User status updated").build());
    }

    @PatchMapping("/users/{userId}/promote")
    @Operation(summary = "Promote a user to ADMIN role")
    public ResponseEntity<MessageResponse> promoteUser(@PathVariable Long userId) {
        adminService.promoteToAdmin(userId);
        return ResponseEntity.ok(MessageResponse.builder().message("User promoted to admin").build());
    }

    @GetMapping("/events/{eventId}/registrations")
    @Operation(summary = "Get all registrations for an event")
    public ResponseEntity<List<RegistrationResponse>> getEventRegistrations(@PathVariable Long eventId) {
        return ResponseEntity.ok(registrationService.getEventRegistrations(eventId));
    }

    @PatchMapping("/attendance")
    @Operation(summary = "Update attendance status for a registration")
    public ResponseEntity<MessageResponse> updateAttendance(@Valid @RequestBody UpdateAttendanceRequest request) {
        registrationService.updateAttendance(request);
        return ResponseEntity.ok(MessageResponse.builder().message("Attendance updated").build());
    }
}
