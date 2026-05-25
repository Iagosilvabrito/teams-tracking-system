package com.tracking.backend.exception;

public record ApiError(
        String code,
        String message,
        String details
) {}
