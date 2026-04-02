package com.eventmanagement.service;

import com.eventmanagement.dto.response.ApiResponses.DashboardStats;
import com.eventmanagement.dto.response.ApiResponses.UserResponse;
import com.eventmanagement.entity.Event;
import com.eventmanagement.entity.User;
import com.eventmanagement.enums.EventCategory;
import com.eventmanagement.enums.Role;
import com.eventmanagement.exception.AppExceptions;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.RegistrationRepository;
import com.eventmanagement.repository.UserRepository;
import com.eventmanagement.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService Tests")
class AdminServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private EventRepository eventRepository;
    @Mock private RegistrationRepository registrationRepository;

    @InjectMocks private AdminServiceImpl adminService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L).firstName("Alice").lastName("Smith")
                .email("alice@example.com").role(Role.ROLE_USER)
                .enabled(true).build();
    }

    @Test
    @DisplayName("getDashboardStats - returns aggregated counts")
    void getDashboardStats_returnsStats() {
        when(eventRepository.countActiveEvents()).thenReturn(10L);
        when(userRepository.count()).thenReturn(50L);
        when(registrationRepository.count()).thenReturn(120L);
        when(eventRepository.findUpcomingEventsInRange(any(), any())).thenReturn(List.of());

        DashboardStats stats = adminService.getDashboardStats();

        assertThat(stats.getTotalEvents()).isEqualTo(10L);
        assertThat(stats.getTotalUsers()).isEqualTo(50L);
        assertThat(stats.getTotalRegistrations()).isEqualTo(120L);
    }

    @Test
    @DisplayName("getAllUsers - returns all users as UserResponse list")
    void getAllUsers_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        List<UserResponse> result = adminService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("alice@example.com");
        assertThat(result.get(0).getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    @DisplayName("toggleUserStatus - flips enabled flag")
    void toggleUserStatus_flipsFlag() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        adminService.toggleUserStatus(1L);

        assertThat(sampleUser.getEnabled()).isFalse();
        verify(userRepository).save(sampleUser);
    }

    @Test
    @DisplayName("toggleUserStatus - not found throws ResourceNotFoundException")
    void toggleUserStatus_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.toggleUserStatus(99L))
                .isInstanceOf(AppExceptions.ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("promoteToAdmin - sets ROLE_ADMIN")
    void promoteToAdmin_setsAdminRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        adminService.promoteToAdmin(1L);

        assertThat(sampleUser.getRole()).isEqualTo(Role.ROLE_ADMIN);
        verify(userRepository).save(sampleUser);
    }

    @Test
    @DisplayName("promoteToAdmin - not found throws ResourceNotFoundException")
    void promoteToAdmin_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.promoteToAdmin(99L))
                .isInstanceOf(AppExceptions.ResourceNotFoundException.class);
    }
}
