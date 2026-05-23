package com.tracking.backend.agent.dto;

import com.tracking.backend.agent.entity.AgentStatus;
import java.time.LocalDateTime;

public record AgentResponse(
        Long id,
        String externalId,
        String name,
        AgentStatus status,
        Double currentLat,
        Double currentLng,
        LocalDateTime lastSeenAt,
        LocalDateTime createdAt
) {}