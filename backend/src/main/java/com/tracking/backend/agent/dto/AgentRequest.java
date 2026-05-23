package com.tracking.backend.agent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.tracking.backend.agent.entity.AgentStatus;

public record AgentRequest(
        @NotBlank(message = "Nome obrigatório")
        String name,
        @NotNull(message = "Status obrigatório")
        AgentStatus status
) {}