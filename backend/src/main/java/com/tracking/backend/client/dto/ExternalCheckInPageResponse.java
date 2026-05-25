package com.tracking.backend.client.dto;

import java.util.List;

public record ExternalCheckInPageResponse(
        List<ExternalCheckIn> data,
        String cursor,
        String syncToken
) {}
