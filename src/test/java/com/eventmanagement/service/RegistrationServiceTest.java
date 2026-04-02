package com.eventmanagement.service;

import com.eventmanagement.dto.request.RegistrationRequest;
import com.eventmanagement.dto.response.ApiResponses.RegistrationResponse;
import com.eventmanagement.entity.Event;
import com.eventmanagement.entity.Registration;
import com.eventmanagement.entity.User;
import com.eventmanagement.enums.EventCategory;
import com.eventmanagement.enums.RegistrationStatus;
import com.eventmanagement.exception.AppExceptions;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.RegistrationRepository;
import com.eventmanagement.repository.UserRepository;
import com.eventmanagement.service.impl.RegistrationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistrationService Tests")
class RegistrationServiceTest {

    @Mock private RegistrationRepository registrationRepository;
    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private EmailService emailService;

    @InjectMocks private RegistrationServiceImpl registrationService;

    private User user;
    private Event event;
    private Registration registration;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("user@example.com")
                .firstName("Jane").lastName("Doe").build();

        event = Event.builder().id(1L).title("Test Event")
                .startDateTime(LocalDateTime.now().plusDays(3))
                .endDateTime(LocalDateTime.now().plusDays(3).plusHours(4))
                .location("Mumbai").category(EventCategory.WORKSHOP)
                .maxCapacity(50).isActive(true).build();

        registration = Registration.builder()
                .id(1L).user(user).event(event)
                .status(RegistrationStatus.REGISTERED).build();
    }

    @Test
    @DisplayName("Register - success")
    void register_success() {
        RegistrationRequest req = new RegistrationRequest();
        req.setEventId(1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.existsByUserIdAndEventIdAndStatusIn(eq(1L), eq(1L), anyList())).thenReturn(false);
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);
        doNothing().when(emailService).sendRegistrationConfirmation(any());

        RegistrationResponse result = registrationService.register(req, "user@example.com");

        assertThat(result.getEventTitle()).isEqualTo("Test Event");
        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.REGISTERED);
        verify(emailService).sendRegistrationConfirmation(any());
    }

    @Test
    @DisplayName("Register - already registered throws DuplicateResourceException")
    void register_alreadyRegistered_throws() {
        RegistrationRequest req = new RegistrationRequest();
        req.setEventId(1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.existsByUserIdAndEventIdAndStatusIn(eq(1L), eq(1L), anyList())).thenReturn(true);

        assertThatThrownBy(() -> registrationService.register(req, "user@example.com"))
                .isInstanceOf(AppExceptions.DuplicateResourceException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    @DisplayName("Register - event full throws EventCapacityException")
    void register_eventFull_throws() {
        // Fill up the event by setting max capacity to 0 effectively
        event = Event.builder().id(1L).title("Full Event")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .location("Delhi").category(EventCategory.CONFERENCE)
                .maxCapacity(0).isActive(true).build();

        RegistrationRequest req = new RegistrationRequest();
        req.setEventId(1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.existsByUserIdAndEventIdAndStatusIn(anyLong(), anyLong(), anyList())).thenReturn(false);

        assertThatThrownBy(() -> registrationService.register(req, "user@example.com"))
                .isInstanceOf(AppExceptions.EventCapacityException.class)
                .hasMessageContaining("fully booked");
    }

    @Test
    @DisplayName("Cancel registration - success")
    void cancelRegistration_success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(registrationRepository.findByUserIdAndEventId(1L, 1L)).thenReturn(Optional.of(registration));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);
        doNothing().when(emailService).sendRegistrationCancellation(any());

        registrationService.cancelRegistration(1L, "user@example.com");

        assertThat(registration.getStatus()).isEqualTo(RegistrationStatus.CANCELLED);
        verify(emailService).sendRegistrationCancellation(any());
    }
}
