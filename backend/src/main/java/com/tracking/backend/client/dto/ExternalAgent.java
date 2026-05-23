package com.tracking.backend.client.dto;

public record ExternalAgent(
        String id,
        String name,
        String status
) {}