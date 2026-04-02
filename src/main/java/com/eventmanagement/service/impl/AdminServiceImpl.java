package com.eventmanagement.service.impl;

import com.eventmanagement.dto.response.ApiResponses.DashboardStats;
import com.eventmanagement.dto.response.ApiResponses.UserResponse;
import com.eventmanagement.entity.User;
import com.eventmanagement.enums.Role;
import com.eventmanagement.exception.AppExceptions;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.RegistrationRepository;
import com.eventmanagement.repository.UserRepository;
import com.eventmanagement.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats() {
        long upcomingEvents = eventRepository.findUpcomingEventsInRange(
                LocalDateTime.now(), LocalDateTime.now().plusYears(1)).size();

        return DashboardStats.builder()
                .totalEvents(eventRepository.countActiveEvents())
                .totalUsers(userRepository.count())
                .totalRegistrations(registrationRepository.count())
                .upcomingEvents(upcomingEvents)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toUserResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("User", userId));
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void promoteToAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("User", userId));
        user.setRole(Role.ROLE_ADMIN);
        userRepository.save(user);
    }

    private UserResponse toUserResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .role(u.getRole())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
