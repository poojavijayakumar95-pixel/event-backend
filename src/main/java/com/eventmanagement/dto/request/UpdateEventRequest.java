package com.eventmanagement.dto.request;

import com.eventmanagement.enums.EventCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateEventRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200)
    private String title;

    @Size(max = 5000)
    private String description;

    @NotNull(message = "Start date/time is required")
    // No @Future here — updating an existing event may keep the same past start date
    private LocalDateTime startDateTime;

    @NotNull(message = "End date/time is required")
    private LocalDateTime endDateTime;

    @NotBlank(message = "Location is required")
    @Size(max = 300)
    private String location;

    @Size(max = 200)
    private String venue;

    @NotNull(message = "Category is required")
    private EventCategory category;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100000)
    private Integer maxCapacity;

    private String imageUrl;
    private List<Long> speakerIds;
}
