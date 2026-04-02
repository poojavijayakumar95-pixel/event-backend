package com.eventmanagement.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SpeakerRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 200)
    private String title;

    @Size(max = 200)
    private String organization;

    @Size(max = 2000)
    private String bio;

    @Size(max = 500)
    private String photoUrl;

    @Size(max = 200)
    private String linkedinUrl;

    @Size(max = 200)
    private String websiteUrl;
}
