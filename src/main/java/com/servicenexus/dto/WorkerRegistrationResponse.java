
package com.servicenexus.dto;

public record WorkerRegistrationResponse(
    Long accountId,
    Long workerProfileId,
    String name,
    String email
) {}
