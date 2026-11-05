
package com.servicenexus.dto;

public record CustomerDashboardResponse(
        long totalBookings,
        long completedBookings,
        long pendingBookings,
        long acceptedBookings,
        long cancelledBookings,
        long rejectedBookings
) {
}
