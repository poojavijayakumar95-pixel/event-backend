package com.eventmanagement.service.impl;

import com.eventmanagement.dto.request.RegistrationRequest;
import com.eventmanagement.dto.request.UpdateAttendanceRequest;
import com.eventmanagement.dto.response.ApiResponses.PagedResponse;
import com.eventmanagement.dto.response.ApiResponses.RegistrationResponse;
import com.eventmanagement.entity.Event;
import com.eventmanagement.entity.Registration;
import com.eventmanagement.entity.User;
import com.eventmanagement.enums.RegistrationStatus;
import com.eventmanagement.exception.AppExceptions;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.RegistrationRepository;
import com.eventmanagement.repository.UserRepository;
import com.eventmanagement.service.EmailService;
import com.eventmanagement.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final List<RegistrationStatus> ACTIVE_STATUSES =
            List.of(RegistrationStatus.REGISTERED, RegistrationStatus.ATTENDED);

    @Override
    @Transactional
    public RegistrationResponse register(RegistrationRequest request, String userEmail) {
        User user = findUserByEmail(userEmail);
        Event event = findActiveEvent(request.getEventId());

        // BUG FIX: only block if an ACTIVE (non-cancelled) registration exists
        boolean activeExists = registrationRepository
                .existsByUserIdAndEventIdAndStatusIn(user.getId(), event.getId(), ACTIVE_STATUSES);
        if (activeExists) {
            throw new AppExceptions.DuplicateResourceException("You are already registered for this event");
        }

        if (!event.hasAvailableSlots()) {
            throw new AppExceptions.EventCapacityException("This event is fully booked");
        }

        // Check if a cancelled record exists — reuse it instead of creating duplicate
        Optional<Registration> existing = registrationRepository
                .findByUserIdAndEventId(user.getId(), event.getId());

        Registration registration;
        if (existing.isPresent()) {
            registration = existing.get();
            registration.setStatus(RegistrationStatus.REGISTERED);
            registration.setNotes(request.getNotes());
        } else {
            registration = Registration.builder()
                    .user(user)
                    .event(event)
                    .status(RegistrationStatus.REGISTERED)
                    .notes(request.getNotes())
                    .build();
        }

        Registration saved = registrationRepository.save(registration);
        emailService.sendRegistrationConfirmation(saved);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void cancelRegistration(Long eventId, String userEmail) {
        User user = findUserByEmail(userEmail);
        Registration registration = registrationRepository
                .findByUserIdAndEventId(user.getId(), eventId)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Registration not found"));

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new AppExceptions.BadRequestException("Registration is already cancelled");
        }
        registration.setStatus(RegistrationStatus.CANCELLED);
        Registration saved = registrationRepository.save(registration);
        emailService.sendRegistrationCancellation(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RegistrationResponse> getUserRegistrations(String userEmail, int page, int size) {
        User user = findUserByEmail(userEmail);
        Page<Registration> regPage = registrationRepository.findByUserId(
                user.getId(), PageRequest.of(page, size, Sort.by("registeredAt").descending()));
        return toPagedResponse(regPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationResponse> getEventRegistrations(Long eventId) {
        if (!eventRepository.existsById(eventId))
            throw new AppExceptions.ResourceNotFoundException("Event", eventId);
        return registrationRepository.findByEventId(eventId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateAttendance(UpdateAttendanceRequest request) {
        Registration reg = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException(
                        "Registration", request.getRegistrationId()));
        reg.setStatus(request.getStatus());
        registrationRepository.save(reg);
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("User not found"));
    }

    private Event findActiveEvent(Long id) {
        return eventRepository.findById(id)
                .filter(Event::getIsActive)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Event", id));
    }

    public RegistrationResponse toResponse(Registration r) {
        return RegistrationResponse.builder()
                .id(r.getId())
                .eventId(r.getEvent().getId())
                .eventTitle(r.getEvent().getTitle())
                .eventStartDateTime(r.getEvent().getStartDateTime())
                .eventLocation(r.getEvent().getLocation())
                .userId(r.getUser().getId())
                .userName(r.getUser().getFullName())
                .userEmail(r.getUser().getEmail())
                .status(r.getStatus())
                .notes(r.getNotes())
                .registeredAt(r.getRegisteredAt())
                .build();
    }

    private PagedResponse<RegistrationResponse> toPagedResponse(Page<Registration> page) {
        return PagedResponse.<RegistrationResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
