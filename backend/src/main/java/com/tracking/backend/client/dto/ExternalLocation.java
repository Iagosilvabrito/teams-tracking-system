package com.tracking.backend.client.dto;

public record ExternalLocation(
        String agentId,
        Double latitude,
        Double longitude,
        String currentAddress,
        Double accuracy,
        Double speed,
        Integer battery,
        String status,
        String lastSeen
) {}