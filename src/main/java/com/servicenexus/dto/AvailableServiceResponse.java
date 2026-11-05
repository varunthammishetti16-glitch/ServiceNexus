package com.servicenexus.dto;

import java.math.BigDecimal;

public record AvailableServiceResponse(
        Long id,
        String name,
        String category,
        String description,
        BigDecimal price,
        String location
) {
}