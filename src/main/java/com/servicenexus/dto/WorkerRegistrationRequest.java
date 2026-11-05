
package com.servicenexus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WorkerRegistrationRequest(
    @NotBlank @Size(max = 100)
    String name,

    @NotBlank @Email @Size(max = 254)
    String email,

    @NotBlank @Size(min = 8, max = 72)
    String password,

    @NotBlank @Size(max = 20)
    String phone,

    @NotBlank @Size(max = 100)
    String skills,

    @NotBlank @Size(max = 200)
    String location,

    @Size(max = 1000)
    String description
) {}
