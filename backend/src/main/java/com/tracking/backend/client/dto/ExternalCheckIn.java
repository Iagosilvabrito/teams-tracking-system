package com.tracking.backend.client.dto;

public record ExternalCheckIn(
        String id,
        String agentId,
        String type,
        String source,
        Double latitude,
        Double longitude,
        String address,
        Double accuracy,
        Double speed,
        String notes,
        Double distanceFromPrevious,
        String externalEventId,
        String occurredAt,
        String syncedAt
) {}