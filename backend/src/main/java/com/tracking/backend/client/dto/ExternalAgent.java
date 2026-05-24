package com.tracking.backend.client.dto;

public record ExternalAgent(
        String id,
        String name,
        String role,
        String team,
        String phone,
        String email,
        boolean active,
        String status,
        Integer battery,
        String lastSeen
) {}