package com.eventmanagement.service;

import com.eventmanagement.dto.request.EventRequest;
import com.eventmanagement.dto.response.ApiResponses.EventResponse;
import com.eventmanagement.dto.response.ApiResponses.PagedResponse;
import com.eventmanagement.entity.Event;
import com.eventmanagement.entity.User;
import com.eventmanagement.enums.EventCategory;
import com.eventmanagement.enums.Role;
import com.eventmanagement.exception.AppExceptions;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.SpeakerRepository;
import com.eventmanagement.repository.UserRepository;
import com.eventmanagement.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Tests")
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private SpeakerRepository speakerRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private EventServiceImpl eventService;

    private Event sampleEvent;
    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(1L).email("admin@example.com").role(Role.ROLE_ADMIN).build();

        sampleEvent = Event.builder()
                .id(1L).title("Spring Conf").description("A great conf")
                .startDateTime(LocalDateTime.now().plusDays(5))
                .endDateTime(LocalDateTime.now().plusDays(5).plusHours(8))
                .location("Bangalore").category(EventCategory.CONFERENCE)
                .maxCapacity(200).isActive(true).build();
    }

    @Test
    @DisplayName("Create event - success")
    void createEvent_success() {
        EventRequest req = buildRequest();
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(eventRepository.save(any(Event.class))).thenReturn(sampleEvent);

        EventResponse result = eventService.createEvent(req, "admin@example.com");

        assertThat(result.getTitle()).isEqualTo("Spring Conf");
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    @DisplayName("Create event - invalid dates throws BadRequestException")
    void createEvent_invalidDates_throws() {
        EventRequest req = buildRequest();
        req.setEndDateTime(req.getStartDateTime().minusHours(1));

        // No stub for userRepository — validateEventDates() throws before it is called,
        // so stubbing findByEmail here would be an UnnecessaryStubbingException
        assertThatThrownBy(() -> eventService.createEvent(req, "admin@example.com"))
                .isInstanceOf(AppExceptions.BadRequestException.class)
                .hasMessageContaining("End date must be after start date");
    }

    @Test
    @DisplayName("Get event by ID - found")
    void getEventById_found() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

        EventResponse result = eventService.getEventById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Get event by ID - not found throws ResourceNotFoundException")
    void getEventById_notFound_throws() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(99L))
                .isInstanceOf(AppExceptions.ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Get all events - returns paged response")
    void getAllEvents_returnsPagedResponse() {
        Page<Event> page = new PageImpl<>(List.of(sampleEvent), PageRequest.of(0, 10), 1);
        when(eventRepository.findByIsActiveTrue(any(Pageable.class))).thenReturn(page);

        PagedResponse<EventResponse> result = eventService.getAllEvents(0, 10);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Delete event - soft deletes")
    void deleteEvent_softDeletes() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(sampleEvent);

        eventService.deleteEvent(1L);

        assertThat(sampleEvent.getIsActive()).isFalse();
        verify(eventRepository).save(sampleEvent);
    }

    private EventRequest buildRequest() {
        EventRequest req = new EventRequest();
        req.setTitle("Spring Conf");
        req.setDescription("A great conf");
        req.setStartDateTime(LocalDateTime.now().plusDays(5));
        req.setEndDateTime(LocalDateTime.now().plusDays(5).plusHours(8));
        req.setLocation("Bangalore");
        req.setCategory(EventCategory.CONFERENCE);
        req.setMaxCapacity(200);
        return req;
    }
}
