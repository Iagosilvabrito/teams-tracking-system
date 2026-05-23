package com.tracking.backend.client.dto;

import java.util.List;

public record AgentPageResponse(
        List<ExternalAgent> data,
        int page,
        int totalPages,
        String syncToken
) {}