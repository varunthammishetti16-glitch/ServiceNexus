
package com.servicenexus.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateBookingRequest(

        @NotNull(message = "Please select a service")
        @Positive(message = "Invalid service selection")
        Long serviceId,

        @NotBlank(message = "Phone number is required")
        @Size(max = 20)
        String customerPhone,

        @NotBlank(message = "Service address is required")
        @Size(max = 500)
        String serviceAddress,

        @NotNull(message = "Please select a booking date")
        @FutureOrPresent(message = "Booking date cannot be in the past")
        LocalDate bookingDate,

        @NotNull(message = "Please select a preferred time")
        LocalTime preferredTime,

        @NotBlank(message = "Please describe the required service")
        @Size(max = 1000)
        String description
) {}
