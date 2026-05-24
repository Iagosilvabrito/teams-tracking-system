package com.tracking.backend.client.mapper;

import com.tracking.backend.agent.entity.Agent;
import com.tracking.backend.agent.entity.AgentStatus;
import com.tracking.backend.client.dto.ExternalAgent;

public class ExternalAgentMapper {

    public static Agent toEntity(ExternalAgent external) {
        Agent agent = new Agent();
        agent.setExternalId(external.id());
        agent.setName(external.name());
        agent.setStatus(external.active() ? AgentStatus.ACTIVE : AgentStatus.INACTIVE);
        return agent;
    }

    public static void updateEntity(Agent agent, ExternalAgent external) {
        agent.setName(external.name());
        agent.setStatus(external.active() ? AgentStatus.ACTIVE : AgentStatus.INACTIVE);
    }
}