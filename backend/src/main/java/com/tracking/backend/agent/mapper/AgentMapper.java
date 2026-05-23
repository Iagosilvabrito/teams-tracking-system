package com.tracking.backend.agent.mapper;

import com.tracking.backend.agent.dto.AgentRequest;
import com.tracking.backend.agent.dto.AgentResponse;
import com.tracking.backend.agent.entity.Agent;

public class AgentMapper {

    public static AgentResponse toResponse(Agent agent) {
        return new AgentResponse(
                agent.getId(),
                agent.getExternalId(),
                agent.getName(),
                agent.getStatus(),
                agent.getCurrentLat(),
                agent.getCurrentLng(),
                agent.getLastSeenAt(),
                agent.getCreatedAt()
        );
    }

    public static Agent toEntity(AgentRequest request) {
        Agent agent = new Agent();
        agent.setName(request.name());
        agent.setStatus(request.status());
        return agent;
    }
}