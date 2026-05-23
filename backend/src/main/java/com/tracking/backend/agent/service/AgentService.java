package com.tracking.backend.agent.service;

import com.tracking.backend.agent.dto.AgentRequest;
import com.tracking.backend.agent.dto.AgentResponse;
import com.tracking.backend.agent.entity.Agent;
import com.tracking.backend.agent.mapper.AgentMapper;
import com.tracking.backend.agent.repository.AgentRepository;
import com.tracking.backend.checkin.repository.CheckInRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;
    private final CheckInRepository checkInRepository;


    public List<AgentResponse> findAll() {
        return agentRepository.findAll()
                .stream()
                .map(AgentMapper::toResponse)
                .toList();
    }

    public AgentResponse findById(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agente não encontrado"));
        return AgentMapper.toResponse(agent);
    }

    public AgentResponse create(AgentRequest request) {
        Agent agent = AgentMapper.toEntity(request);
        return AgentMapper.toResponse(agentRepository.save(agent));
    }

    public AgentResponse update(Long id, AgentRequest request) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agente não encontrado"));
        agent.setName(request.name());
        agent.setStatus(request.status());
        return AgentMapper.toResponse(agentRepository.save(agent));
    }

    public void delete(Long id) {
        if (!agentRepository.existsById(id)) {
            throw new EntityNotFoundException("Agente não encontrado");
        }
        agentRepository.deleteById(id);
    }


}