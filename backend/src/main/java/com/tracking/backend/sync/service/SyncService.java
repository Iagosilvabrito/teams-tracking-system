package com.tracking.backend.sync.service;

import com.tracking.backend.agent.entity.Agent;
import com.tracking.backend.agent.repository.AgentRepository;
import com.tracking.backend.agent.entity.AgentStatus;
import com.tracking.backend.client.GpsApiClient;
import com.tracking.backend.client.dto.AgentPageResponse;
import com.tracking.backend.client.dto.AgentLocationPageResponse;
import com.tracking.backend.client.dto.ExternalAgent;
import com.tracking.backend.client.dto.ExternalLocation;
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
            int page = 0;
            int processed = 0;
            String syncToken = syncTokenStore.getToken("AGENT_SYNC");

            while (true) {
                AgentPageResponse response = gpsApiClient.getAgents(page, syncToken);
                if (response == null || response.data().isEmpty()) break;

                for (ExternalAgent external : response.data()) {
                    Optional<Agent> existing = agentRepository.findByExternalId(external.id());
                    if (existing.isPresent()) {
                        Agent agent = existing.get();
                        agent.setName(external.name());
                        agent.setStatus("ACTIVE".equals(external.status()) ? AgentStatus.ACTIVE : AgentStatus.INACTIVE);
                        agentRepository.save(agent);
                    } else {
                        Agent agent = new Agent();
                        agent.setExternalId(external.id());
                        agent.setName(external.name());
                        agent.setStatus("ACTIVE".equals(external.status()) ? AgentStatus.ACTIVE : AgentStatus.INACTIVE);
                        agentRepository.save(agent);
                    }
                    processed++;
                }

                if (response.syncToken() != null) {
                    syncTokenStore.saveToken("AGENT_SYNC", response.syncToken());
                }

                if (page >= response.totalPages() - 1) break;
                page++;
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
            int page = 0;
            int processed = 0;
            String syncToken = syncTokenStore.getToken("POSITION_SYNC");

            while (true) {
                AgentLocationPageResponse response = gpsApiClient.getLocations(page, syncToken);
                if (response == null || response.data().isEmpty()) break;

                for (ExternalLocation external : response.data()) {
                    agentRepository.findByExternalId(external.agentId()).ifPresent(agent -> {
                        boolean exists = locationRepository.existsByAgentIdAndRecordedAt(
                                agent.getId(), external.recordedAt()
                        );
                        if (!exists) {
                            if (external.accuracy() == null || external.accuracy() <= 100) {
                                Location location = new Location();
                                location.setAgent(agent);
                                location.setLat(external.lat());
                                location.setLng(external.lng());
                                location.setAccuracy(external.accuracy());
                                location.setRecordedAt(external.recordedAt());
                                locationRepository.save(location);

                                agent.setCurrentLat(external.lat());
                                agent.setCurrentLng(external.lng());
                                agent.setLastSeenAt(external.recordedAt());
                                agentRepository.save(agent);
                            }
                        }
                    });
                    processed++;
                }

                if (response.syncToken() != null) {
                    syncTokenStore.saveToken("POSITION_SYNC", response.syncToken());
                }

                if (page >= response.totalPages() - 1) break;
                page++;
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