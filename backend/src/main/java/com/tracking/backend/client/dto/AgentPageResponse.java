package com.tracking.backend.client.dto;

import java.util.List;

public record AgentPageResponse(
        List<ExternalAgent> data,
        Integer page,
        Integer totalPages,
        String syncToken
) {}
