
package com.servicenexus.dto;

import com.servicenexus.model.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BookingResponse(
        Long id,
        String serviceName,
        String serviceCategory,
        BigDecimal price,
        String customerName,
        String customerEmail,
        String customerPhone,
        String serviceAddress,
        LocalDate bookingDate,
        LocalTime preferredTime,
        String description,
        BookingStatus status,
        LocalDateTime createdAt
) {}
