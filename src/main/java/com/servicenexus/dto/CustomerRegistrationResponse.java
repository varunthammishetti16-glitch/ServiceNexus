package com.servicenexus.dto;

import java.time.LocalDateTime;

public record CustomerRegistrationResponse(
    Long id,
    String name,
    String email,
    String phone,
    String address,
    LocalDateTime createdAt
) {
}