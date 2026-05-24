package com.tracking.backend.client.dto;

import java.time.LocalDateTime;

public record ExternalLocation(
        String agentId,
        Double latitude,
        Double longitude,
        String currentAddress,
        Double accuracy,
        Double speed,
        Integer battery,
        String status,
        String lastSeen,
        LocalDateTime recordedAt
) {}