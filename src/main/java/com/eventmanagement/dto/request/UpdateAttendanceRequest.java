package com.eventmanagement.dto.request;

import com.eventmanagement.enums.RegistrationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAttendanceRequest {

    @NotNull(message = "Registration ID is required")
    private Long registrationId;

    @NotNull(message = "Status is required")
    private RegistrationStatus status;
}
