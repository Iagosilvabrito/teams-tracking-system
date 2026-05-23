package com.tracking.backend.agent.repository;

import com.tracking.backend.agent.entity.Agent;
import com.tracking.backend.agent.entity.AgentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    List<Agent> findByStatus(AgentStatus status);
    Optional<Agent> findByExternalId(String externalId);
}