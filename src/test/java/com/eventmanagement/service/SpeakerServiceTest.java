package com.eventmanagement.service;

import com.eventmanagement.dto.request.SpeakerRequest;
import com.eventmanagement.dto.response.ApiResponses.SpeakerResponse;
import com.eventmanagement.entity.Speaker;
import com.eventmanagement.exception.AppExceptions;
import com.eventmanagement.repository.SpeakerRepository;
import com.eventmanagement.service.impl.SpeakerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpeakerService Tests")
class SpeakerServiceTest {

    @Mock private SpeakerRepository speakerRepository;
    @InjectMocks private SpeakerServiceImpl speakerService;

    private Speaker sampleSpeaker;
    private SpeakerRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleSpeaker = Speaker.builder()
                .id(1L).firstName("Jane").lastName("Doe")
                .email("jane@speaker.com").title("Senior Engineer")
                .organization("TechCorp").bio("Experienced engineer.").build();

        sampleRequest = new SpeakerRequest();
        sampleRequest.setFirstName("Jane");
        sampleRequest.setLastName("Doe");
        sampleRequest.setEmail("jane@speaker.com");
        sampleRequest.setTitle("Senior Engineer");
        sampleRequest.setOrganization("TechCorp");
        sampleRequest.setBio("Experienced engineer.");
    }

    @Test
    @DisplayName("Create speaker - success")
    void createSpeaker_success() {
        when(speakerRepository.existsByEmail("jane@speaker.com")).thenReturn(false);
        when(speakerRepository.save(any(Speaker.class))).thenReturn(sampleSpeaker);

        SpeakerResponse result = speakerService.createSpeaker(sampleRequest);

        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getEmail()).isEqualTo("jane@speaker.com");
        verify(speakerRepository).save(any(Speaker.class));
    }

    @Test
    @DisplayName("Create speaker - duplicate email throws DuplicateResourceException")
    void createSpeaker_duplicateEmail_throws() {
        when(speakerRepository.existsByEmail("jane@speaker.com")).thenReturn(true);

        assertThatThrownBy(() -> speakerService.createSpeaker(sampleRequest))
                .isInstanceOf(AppExceptions.DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Get speaker by ID - found")
    void getSpeakerById_found() {
        when(speakerRepository.findById(1L)).thenReturn(Optional.of(sampleSpeaker));

        SpeakerResponse result = speakerService.getSpeakerById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLastName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("Get speaker by ID - not found throws ResourceNotFoundException")
    void getSpeakerById_notFound_throws() {
        when(speakerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> speakerService.getSpeakerById(99L))
                .isInstanceOf(AppExceptions.ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Get all speakers - returns list")
    void getAllSpeakers_returnsList() {
        when(speakerRepository.findAll()).thenReturn(List.of(sampleSpeaker));

        List<SpeakerResponse> result = speakerService.getAllSpeakers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrganization()).isEqualTo("TechCorp");
    }

    @Test
    @DisplayName("Delete speaker - success")
    void deleteSpeaker_success() {
        when(speakerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(speakerRepository).deleteById(1L);

        assertThatCode(() -> speakerService.deleteSpeaker(1L)).doesNotThrowAnyException();
        verify(speakerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete speaker - not found throws ResourceNotFoundException")
    void deleteSpeaker_notFound_throws() {
        when(speakerRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> speakerService.deleteSpeaker(99L))
                .isInstanceOf(AppExceptions.ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Search speakers by name")
    void searchSpeakers_byName() {
        when(speakerRepository.searchByName("jane")).thenReturn(List.of(sampleSpeaker));

        List<SpeakerResponse> result = speakerService.searchSpeakers("jane");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Jane");
    }
}
