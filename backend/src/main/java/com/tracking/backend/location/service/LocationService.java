package com.tracking.backend.location.service;

import com.tracking.backend.agent.repository.AgentRepository;
import com.tracking.backend.location.dto.LocationResponse;
import com.tracking.backend.location.dto.RouteHistoryResponse;
import com.tracking.backend.location.entity.Location;
import com.tracking.backend.location.repository.LocationRepository;
import com.tracking.backend.util.HaversineUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final AgentRepository agentRepository;

    public RouteHistoryResponse getRouteHistory(Long agentId) {
        var agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new EntityNotFoundException("Agente não encontrado"));

        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<Location> locations = locationRepository
                .findByAgentIdAndRecordedAtBetweenOrderByRecordedAtAsc(agentId, startOfDay, now);

        List<LocationResponse> locationResponses = locations.stream()
                .map(l -> new LocationResponse(l.getId(), l.getLat(), l.getLng(), l.getAccuracy(), l.getRecordedAt()))
                .toList();

        double totalDistance = 0;
        for (int i = 1; i < locations.size(); i++) {
            Location prev = locations.get(i - 1);
            Location curr = locations.get(i);
            totalDistance += HaversineUtil.calculateDistance(
                    prev.getLat(), prev.getLng(),
                    curr.getLat(), curr.getLng()
            );
        }

        return new RouteHistoryResponse(agentId, agent.getName(), locationResponses, totalDistance);
    }
}