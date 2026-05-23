package com.tracking.backend.location.dto;

import java.util.List;

public record RouteHistoryResponse(
        Long agentId,
        String agentName,
        List<LocationResponse> locations,
        double totalDistanceKm
) {}