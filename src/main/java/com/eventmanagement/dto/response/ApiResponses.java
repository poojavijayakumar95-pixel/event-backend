package com.eventmanagement.dto.response;

import com.eventmanagement.enums.EventCategory;
import com.eventmanagement.enums.RegistrationStatus;
import com.eventmanagement.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ApiResponses {

    // ─── Auth ─────────────────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private UserResponse user;
    }

    // ─── User ─────────────────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private Role role;
        private LocalDateTime createdAt;
    }

    // ─── Speaker ──────────────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SpeakerResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String title;
        private String organization;
        private String bio;
        private String photoUrl;
        private String linkedinUrl;
        private String websiteUrl;
    }

    // ─── Event ────────────────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class EventResponse {
        private Long id;
        private String title;
        private String description;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String location;
        private String venue;
        private EventCategory category;
        private Integer maxCapacity;
        private Integer registeredCount;
        private Integer availableSlots;
        private Boolean isActive;
        private String imageUrl;
        private List<SpeakerResponse> speakers;
        private LocalDateTime createdAt;
    }

    // ─── Registration ─────────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RegistrationResponse {
        private Long id;
        private Long eventId;
        private String eventTitle;
        private LocalDateTime eventStartDateTime;
        private String eventLocation;
        private Long userId;
        private String userName;
        private String userEmail;
        private RegistrationStatus status;
        private String notes;
        private LocalDateTime registeredAt;
    }

    // ─── Generic paginated wrapper ────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PagedResponse<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }

    // ─── Dashboard stats (admin) ─────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DashboardStats {
        private long totalEvents;
        private long totalUsers;
        private long totalRegistrations;
        private long upcomingEvents;
    }

    // ─── Generic message ─────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MessageResponse {
        private String message;
    }
}
