package com.tracking.backend.sync.service;

import com.tracking.backend.agent.entity.Agent;
import com.tracking.backend.agent.repository.AgentRepository;
import com.tracking.backend.client.GpsApiClient;
import com.tracking.backend.client.dto.AgentPageResponse;
import com.tracking.backend.client.dto.AgentLocationPageResponse;
import com.tracking.backend.client.dto.ExternalAgent;
import com.tracking.backend.client.mapper.ExternalAgentMapper;
import com.tracking.backend.client.dto.ExternalLocation;
import com.tracking.backend.client.mapper.ExternalLocationMapper;
import com.tracking.backend.location.entity.Location;
import com.tracking.backend.location.repository.LocationRepository;
import com.tracking.backend.sync.SyncTokenStore;
import com.tracking.backend.sync.entity.SyncLog;
import com.tracking.backend.sync.entity.SyncStatus;
import com.tracking.backend.sync.repository.SyncLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

    private final GpsApiClient gpsApiClient;
    private final AgentRepository agentRepository;
    private final LocationRepository locationRepository;
    private final SyncLogRepository syncLogRepository;
    private final SyncTokenStore syncTokenStore;

    public void syncAgents() {
        SyncLog syncLog = new SyncLog();
        syncLog.setStatus(SyncStatus.RUNNING);
        syncLog.setSyncType("AGENT_SYNC");
        syncLogRepository.save(syncLog);

        try {
            int processed = 0;
            String syncToken = syncTokenStore.getToken("AGENT_SYNC");

            AgentPageResponse response = gpsApiClient.getAgents(0, syncToken);
            if (response != null && !response.data().isEmpty()) {
                for (ExternalAgent external : response.data()) {
                    Optional<Agent> existing = agentRepository.findByExternalId(external.id());
                    if (existing.isPresent()) {
                        ExternalAgentMapper.updateEntity(existing.get(), external);
                        agentRepository.save(existing.get());
                    } else {
                        agentRepository.save(ExternalAgentMapper.toEntity(external));
                    }
                    processed++;
                }
            }

            syncLog.setStatus(SyncStatus.SUCCESS);
            syncLog.setRecordsProcessed(processed);
        } catch (Exception e) {
            log.error("Erro ao sincronizar agentes: {}", e.getMessage());
            syncLog.setStatus(SyncStatus.FAILED);
            syncLog.setErrorMessage(e.getMessage());
        } finally {
            syncLog.setFinishedAt(LocalDateTime.now());
            syncLogRepository.save(syncLog);
        }
    }

    public void syncLocations() {
        SyncLog syncLog = new SyncLog();
        syncLog.setStatus(SyncStatus.RUNNING);
        syncLog.setSyncType("POSITION_SYNC");
        syncLogRepository.save(syncLog);

        try {
            int processed = 0;
            String syncToken = syncTokenStore.getToken("POSITION_SYNC");

            AgentLocationPageResponse response = gpsApiClient.getLocations(0, syncToken);
            if (response != null && !response.data().isEmpty()) {
                for (ExternalLocation external : response.data()) {
                    agentRepository.findByExternalId(external.agentId()).ifPresent(agent -> {
                        LocalDateTime lastSeen = external.lastSeen() != null
                                ? OffsetDateTime.parse(external.lastSeen()).toLocalDateTime()
                                : null;

                        boolean exists = lastSeen != null && locationRepository
                                .existsByAgentIdAndRecordedAt(agent.getId(), lastSeen);

                        if (!exists) {
                            if (external.latitude() != null && external.longitude() != null &&
                                    (external.accuracy() == null || external.accuracy() <= 50)) {
                                Location location = ExternalLocationMapper.toEntity(external, agent, lastSeen);
                                locationRepository.save(location);

                                agent.setCurrentLat(external.latitude());
                                agent.setCurrentLng(external.longitude());
                                agent.setLastSeenAt(lastSeen);
                                agentRepository.save(agent);
                            }
                        }
                    });
                    processed++;
                }
            }

            syncLog.setStatus(SyncStatus.SUCCESS);
            syncLog.setRecordsProcessed(processed);
        } catch (Exception e) {
            log.error("Erro ao sincronizar localizações: {}", e.getMessage());
            syncLog.setStatus(SyncStatus.FAILED);
            syncLog.setErrorMessage(e.getMessage());
        } finally {
            syncLog.setFinishedAt(LocalDateTime.now());
            syncLogRepository.save(syncLog);
        }
    }
}