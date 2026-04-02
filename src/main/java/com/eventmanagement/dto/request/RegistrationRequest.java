package com.eventmanagement.dto.request;

import com.eventmanagement.enums.RegistrationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @Size(max = 500)
    private String notes;
}
