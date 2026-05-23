package com.tracking.backend.checkin.dto;

import java.time.LocalDateTime;

public record CheckInResponse(
        Long id,
        Long agentId,
        String agentName,
        Double lat,
        Double lng,
        String address,
        String notes,
        LocalDateTime checkedInAt
) {}