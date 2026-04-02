package com.eventmanagement.service.impl;

import com.eventmanagement.dto.request.EventRequest;
import com.eventmanagement.dto.request.UpdateEventRequest;
import com.eventmanagement.dto.response.ApiResponses.*;
import com.eventmanagement.entity.Event;
import com.eventmanagement.entity.Speaker;
import com.eventmanagement.entity.User;
import com.eventmanagement.enums.EventCategory;
import com.eventmanagement.exception.AppExceptions;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.SpeakerRepository;
import com.eventmanagement.repository.UserRepository;
import com.eventmanagement.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final SpeakerRepository speakerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EventResponse createEvent(EventRequest request, String adminEmail) {
        validateEventDates(request);

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("User not found"));

        List<Speaker> speakers = resolveSpeakers(request.getSpeakerIds());

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .location(request.getLocation())
                .venue(request.getVenue())
                .category(request.getCategory())
                .maxCapacity(request.getMaxCapacity() != null ? request.getMaxCapacity() : 100)
                .imageUrl(request.getImageUrl())
                .isActive(true)
                .createdBy(admin)
                .speakers(speakers)
                .build();

        return toResponse(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long id, UpdateEventRequest request) {
        Event event = findActiveEvent(id);
        validateEventDates(request);

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setLocation(request.getLocation());
        event.setVenue(request.getVenue());
        event.setCategory(request.getCategory());
        if (request.getMaxCapacity() != null) event.setMaxCapacity(request.getMaxCapacity());
        if (request.getImageUrl() != null) event.setImageUrl(request.getImageUrl());

        // FIX: Never replace the managed collection reference — Hibernate owns it.
        // clear() + addAll() mutates in-place; setSpeakers() swaps the reference
        // which causes UnsupportedOperationException when Hibernate tries to .clear() it.
        List<Speaker> newSpeakers = resolveSpeakers(request.getSpeakerIds());
        event.getSpeakers().clear();
        event.getSpeakers().addAll(newSpeakers);

        return toResponse(eventRepository.save(event));
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = findActiveEvent(id);
        event.setIsActive(false);
        eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        return toResponse(findActiveEvent(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EventResponse> getAllEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime").ascending());
        Page<Event> eventPage = eventRepository.findByIsActiveTrue(pageable);
        return toPagedResponse(eventPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EventResponse> searchEvents(String search, EventCategory category,
                                                      String location, LocalDateTime startDate,
                                                      LocalDateTime endDate, Pageable pageable) {
        Page<Event> page = eventRepository.searchEvents(search, category, location, startDate, endDate, pageable);
        return toPagedResponse(page);
    }

    private Event findActiveEvent(Long id) {
        return eventRepository.findById(id)
                .filter(Event::getIsActive)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Event", id));
    }

    private List<Speaker> resolveSpeakers(List<Long> speakerIds) {
        // Must return a mutable ArrayList — Hibernate needs to call .clear() on it during merge
        if (speakerIds == null || speakerIds.isEmpty()) return new ArrayList<>();
        return speakerIds.stream()
                .map(sid -> speakerRepository.findById(sid)
                        .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Speaker", sid)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void validateEventDates(EventRequest request) {
        if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
            throw new AppExceptions.BadRequestException("End date must be after start date");
        }
    }

    private void validateEventDates(UpdateEventRequest request) {
        if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
            throw new AppExceptions.BadRequestException("End date must be after start date");
        }
    }

    public EventResponse toResponse(Event event) {
        List<SpeakerResponse> speakerResponses = event.getSpeakers().stream()
                .map(s -> SpeakerResponse.builder()
                        .id(s.getId())
                        .firstName(s.getFirstName())
                        .lastName(s.getLastName())
                        .email(s.getEmail())
                        .title(s.getTitle())
                        .organization(s.getOrganization())
                        .bio(s.getBio())
                        .photoUrl(s.getPhotoUrl())
                        .linkedinUrl(s.getLinkedinUrl())
                        .websiteUrl(s.getWebsiteUrl())
                        .build())
                .collect(Collectors.toList());

        int registered = event.getRegisteredCount();
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .location(event.getLocation())
                .venue(event.getVenue())
                .category(event.getCategory())
                .maxCapacity(event.getMaxCapacity())
                .registeredCount(registered)
                .availableSlots(event.getMaxCapacity() - registered)
                .isActive(event.getIsActive())
                .imageUrl(event.getImageUrl())
                .speakers(speakerResponses)
                .createdAt(event.getCreatedAt())
                .build();
    }

    private PagedResponse<EventResponse> toPagedResponse(Page<Event> page) {
        return PagedResponse.<EventResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
