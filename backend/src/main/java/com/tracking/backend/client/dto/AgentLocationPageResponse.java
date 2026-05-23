package com.tracking.backend.client.dto;

import java.util.List;

public record AgentLocationPageResponse(
        List<ExternalLocation> data,
        int page,
        int totalPages,
        String syncToken
) {}