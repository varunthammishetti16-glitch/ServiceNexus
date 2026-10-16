
package com.servicenexus.cooperative.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCooperativeRequest(
    @NotBlank
    @Size(max = 120)
    String name,

    @Size(max = 1000)
    String description,

    @NotBlank
    @Size(max = 150)
    String location,

    @Size(max = 500)
    String servicesOffered,

    @Email
    @Size(max = 254)
    String contactEmail
) {}
