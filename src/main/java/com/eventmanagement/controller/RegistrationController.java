package com.eventmanagement.controller;

import com.eventmanagement.dto.request.RegistrationRequest;
import com.eventmanagement.dto.response.ApiResponses.MessageResponse;
import com.eventmanagement.dto.response.ApiResponses.PagedResponse;
import com.eventmanagement.dto.response.ApiResponses.RegistrationResponse;
import com.eventmanagement.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
@Tag(name = "Registrations", description = "Register for events, view and cancel registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @Operation(summary = "Register current user for an event")
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody RegistrationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.register(request, userDetails.getUsername()));
    }

    @DeleteMapping("/events/{eventId}")
    @Operation(summary = "Cancel registration for an event")
    public ResponseEntity<MessageResponse> cancel(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        registrationService.cancelRegistration(eventId, userDetails.getUsername());
        return ResponseEntity.ok(MessageResponse.builder().message("Registration cancelled successfully").build());
    }

    @GetMapping("/my")
    @Operation(summary = "Get current user's registrations")
    public ResponseEntity<PagedResponse<RegistrationResponse>> myRegistrations(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                registrationService.getUserRegistrations(userDetails.getUsername(), page, size));
    }
}
