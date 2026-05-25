package com.tracking.backend.client.dto;

import java.util.List;

public record AgentLocationPageResponse(
        List<ExternalLocation> data,
        Integer page,
        Integer totalPages,
        String syncToken
) {}
