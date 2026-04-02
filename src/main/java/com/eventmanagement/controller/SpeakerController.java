package com.eventmanagement.controller;

import com.eventmanagement.dto.request.SpeakerRequest;
import com.eventmanagement.dto.response.ApiResponses.SpeakerResponse;
import com.eventmanagement.service.SpeakerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/speakers")
@RequiredArgsConstructor
@Tag(name = "Speakers", description = "Manage event speakers")
public class SpeakerController {

    private final SpeakerService speakerService;

    @GetMapping
    @Operation(summary = "Get all speakers")
    public ResponseEntity<List<SpeakerResponse>> getAllSpeakers(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(speakerService.searchSpeakers(search));
        }
        return ResponseEntity.ok(speakerService.getAllSpeakers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get speaker by ID")
    public ResponseEntity<SpeakerResponse> getSpeaker(@PathVariable Long id) {
        return ResponseEntity.ok(speakerService.getSpeakerById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a speaker (Admin)")
    public ResponseEntity<SpeakerResponse> createSpeaker(@Valid @RequestBody SpeakerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(speakerService.createSpeaker(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a speaker (Admin)")
    public ResponseEntity<SpeakerResponse> updateSpeaker(
            @PathVariable Long id, @Valid @RequestBody SpeakerRequest request) {
        return ResponseEntity.ok(speakerService.updateSpeaker(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a speaker (Admin)")
    public ResponseEntity<Void> deleteSpeaker(@PathVariable Long id) {
        speakerService.deleteSpeaker(id);
        return ResponseEntity.noContent().build();
    }
}
