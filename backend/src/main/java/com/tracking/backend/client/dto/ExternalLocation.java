package com.tracking.backend.client.dto;

import java.time.LocalDateTime;

public record ExternalLocation(
        String agentId,
        Double lat,
        Double lng,
        Double accuracy,
        LocalDateTime recordedAt
) {}