package com.eventmanagement.service.impl;

import com.eventmanagement.dto.request.SpeakerRequest;
import com.eventmanagement.dto.response.ApiResponses.SpeakerResponse;
import com.eventmanagement.entity.Speaker;
import com.eventmanagement.exception.AppExceptions;
import com.eventmanagement.repository.SpeakerRepository;
import com.eventmanagement.service.SpeakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpeakerServiceImpl implements SpeakerService {

    private final SpeakerRepository speakerRepository;

    @Override
    @Transactional
    public SpeakerResponse createSpeaker(SpeakerRequest request) {
        if (speakerRepository.existsByEmail(request.getEmail())) {
            throw new AppExceptions.DuplicateResourceException("Speaker already exists with email: " + request.getEmail());
        }
        Speaker speaker = fromRequest(request, new Speaker());
        return toResponse(speakerRepository.save(speaker));
    }

    @Override
    @Transactional
    public SpeakerResponse updateSpeaker(Long id, SpeakerRequest request) {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Speaker", id));

        speakerRepository.findByEmail(request.getEmail())
                .filter(s -> !s.getId().equals(id))
                .ifPresent(s -> { throw new AppExceptions.DuplicateResourceException("Email already in use"); });

        return toResponse(speakerRepository.save(fromRequest(request, speaker)));
    }

    @Override
    @Transactional
    public void deleteSpeaker(Long id) {
        if (!speakerRepository.existsById(id)) {
            throw new AppExceptions.ResourceNotFoundException("Speaker", id);
        }
        speakerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SpeakerResponse getSpeakerById(Long id) {
        return toResponse(speakerRepository.findById(id)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Speaker", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpeakerResponse> getAllSpeakers() {
        return speakerRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpeakerResponse> searchSpeakers(String name) {
        return speakerRepository.searchByName(name).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private Speaker fromRequest(SpeakerRequest request, Speaker speaker) {
        speaker.setFirstName(request.getFirstName());
        speaker.setLastName(request.getLastName());
        speaker.setEmail(request.getEmail());
        speaker.setTitle(request.getTitle());
        speaker.setOrganization(request.getOrganization());
        speaker.setBio(request.getBio());
        speaker.setPhotoUrl(request.getPhotoUrl());
        speaker.setLinkedinUrl(request.getLinkedinUrl());
        speaker.setWebsiteUrl(request.getWebsiteUrl());
        return speaker;
    }

    public SpeakerResponse toResponse(Speaker s) {
        return SpeakerResponse.builder()
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
                .build();
    }
}
