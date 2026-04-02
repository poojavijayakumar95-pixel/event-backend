package com.eventmanagement.service;

import com.eventmanagement.dto.request.SpeakerRequest;
import com.eventmanagement.dto.response.ApiResponses.SpeakerResponse;

import java.util.List;

public interface SpeakerService {
    SpeakerResponse createSpeaker(SpeakerRequest request);
    SpeakerResponse updateSpeaker(Long id, SpeakerRequest request);
    void deleteSpeaker(Long id);
    SpeakerResponse getSpeakerById(Long id);
    List<SpeakerResponse> getAllSpeakers();
    List<SpeakerResponse> searchSpeakers(String name);
}
