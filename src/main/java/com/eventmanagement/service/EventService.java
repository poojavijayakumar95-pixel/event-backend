package com.eventmanagement.service;

import com.eventmanagement.dto.request.EventRequest;
import com.eventmanagement.dto.request.UpdateEventRequest;
import com.eventmanagement.dto.response.ApiResponses.EventResponse;
import com.eventmanagement.dto.response.ApiResponses.PagedResponse;
import com.eventmanagement.enums.EventCategory;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EventService {
    EventResponse createEvent(EventRequest request, String adminEmail);
    EventResponse updateEvent(Long id, UpdateEventRequest request);   // FIX: use UpdateEventRequest
    void deleteEvent(Long id);
    EventResponse getEventById(Long id);
    PagedResponse<EventResponse> getAllEvents(int page, int size);
    PagedResponse<EventResponse> searchEvents(String search, EventCategory category,
                                               String location, LocalDateTime startDate,
                                               LocalDateTime endDate, Pageable pageable);
}
