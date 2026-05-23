package com.tracking.backend.checkin.service;

import com.tracking.backend.checkin.mapper.CheckInMapper;
import com.tracking.backend.checkin.dto.CheckInRequest;
import com.tracking.backend.checkin.dto.CheckInResponse;
import com.tracking.backend.agent.entity.Agent;
import com.tracking.backend.agent.repository.AgentRepository;
import com.tracking.backend.checkin.repository.CheckInRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final AgentRepository agentRepository;
    private final CheckInRepository checkInRepository;

    public CheckInResponse checkIn(Long agentId, CheckInRequest request) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new EntityNotFoundException("Agente não encontrado"));

        return CheckInMapper.toResponse(checkInRepository.save(CheckInMapper.toEntity(request, agent)));
    }

    public List<CheckInResponse> findByAgent(Long agentId) {
        if (!agentRepository.existsById(agentId)) {
            throw new EntityNotFoundException("Agente não encontrado");
        }
        return checkInRepository.findByAgentIdOrderByCheckedInAtDesc(agentId)
                .stream()
                .map(CheckInMapper::toResponse)
                .toList();
    }
}