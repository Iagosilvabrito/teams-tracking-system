package com.tracking.backend.checkin.dto;

import jakarta.validation.constraints.NotNull;

public record CheckInRequest(
        @NotNull(message = "Latitude obrigatória")
        Double lat,
        @NotNull(message = "Longitude obrigatória")
        Double lng,
        String address,
        String notes
) {}