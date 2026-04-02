package com.eventmanagement.service;

import com.eventmanagement.dto.request.RegistrationRequest;
import com.eventmanagement.dto.request.UpdateAttendanceRequest;
import com.eventmanagement.dto.response.ApiResponses.PagedResponse;
import com.eventmanagement.dto.response.ApiResponses.RegistrationResponse;

import java.util.List;

public interface RegistrationService {
    RegistrationResponse register(RegistrationRequest request, String userEmail);
    void cancelRegistration(Long eventId, String userEmail);
    PagedResponse<RegistrationResponse> getUserRegistrations(String userEmail, int page, int size);
    List<RegistrationResponse> getEventRegistrations(Long eventId);
    void updateAttendance(UpdateAttendanceRequest request);
}
