package com.tracking.backend.location.dto;

import java.time.LocalDateTime;

public record LocationResponse(
        Long id,
        Double lat,
        Double lng,
        Double accuracy,
        LocalDateTime recordedAt
) {}