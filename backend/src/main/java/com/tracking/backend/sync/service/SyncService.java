package com.tracking.backend.sync.service;

import com.tracking.backend.agent.entity.Agent;
import com.tracking.backend.agent.repository.AgentRepository;
import com.tracking.backend.checkin.entity.CheckIn;
import com.tracking.backend.checkin.repository.CheckInRepository;
import com.tracking.backend.client.GpsApiClient;
import com.tracking.backend.client.dto.*;
import com.tracking.backend.client.mapper.ExternalAgentMapper;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

    private final GpsApiClient gpsApiClient;
    private final AgentRepository agentRepository;
    private final LocationRepository locationRepository;
    private final CheckInRepository checkInRepository;
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

            int page = 0;
            int totalPages = 1;
            String nextSyncToken = syncToken;
            while (page < totalPages) {
                AgentPageResponse response = gpsApiClient.getAgents(page, syncToken);
                if (response == null) {
                    break;
                }

                totalPages = response.totalPages() != null && response.totalPages() > 0
                        ? response.totalPages()
                        : 1;
                nextSyncToken = response.syncToken() != null ? response.syncToken() : nextSyncToken;

                for (ExternalAgent external : safeList(response.data())) {
                    Optional<Agent> existing = agentRepository.findByExternalId(external.id());
                    if (existing.isPresent()) {
                        ExternalAgentMapper.updateEntity(existing.get(), external);
                        agentRepository.save(existing.get());
                    } else {
                        agentRepository.save(ExternalAgentMapper.toEntity(external));
                    }
                    processed++;
                }
                page++;
            }

            if (nextSyncToken != null) {
                syncTokenStore.saveToken("AGENT_SYNC", nextSyncToken);
                syncLog.setSyncToken(nextSyncToken);
            }

            syncLog.setStatus(SyncStatus.SUCCESS);
            syncLog.setRecordsProcessed(processed);
        } catch (Exception e) {
            log.error("Erro ao sincronizar agentes: {}", e.getMessage());
            syncLog.setStatus(SyncStatus.FAILED);
            syncLog.setErrorMessage(e.getMessage());
            throw new IllegalStateException("Erro ao sincronizar agentes.", e);
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
            AtomicInteger processed = new AtomicInteger();
            String syncToken = syncTokenStore.getToken("POSITION_SYNC");

            int page = 0;
            int totalPages = 1;
            String nextSyncToken = syncToken;
            while (page < totalPages) {
                AgentLocationPageResponse response = gpsApiClient.getLocations(page, syncToken);
                if (response == null) {
                    break;
                }

                totalPages = response.totalPages() != null && response.totalPages() > 0
                        ? response.totalPages()
                        : 1;
                nextSyncToken = response.syncToken() != null ? response.syncToken() : nextSyncToken;

                for (ExternalLocation external : safeList(response.data())) {
                    agentRepository.findByExternalId(external.agentId()).ifPresent(agent -> {
                        LocalDateTime lastSeen = external.lastSeen() != null
                                ? OffsetDateTime.parse(external.lastSeen()).toLocalDateTime()
                                : null;

                        boolean exists = lastSeen != null && locationRepository
                                .existsByAgentIdAndRecordedAt(agent.getId(), lastSeen);

                        if (!exists) {
                            if (hasUsableGps(external.latitude(), external.longitude(), external.accuracy())) {
                                Location location = ExternalLocationMapper.toEntity(external, agent, lastSeen);
                                locationRepository.save(location);

                                agent.setCurrentLat(external.latitude());
                                agent.setCurrentLng(external.longitude());
                                agent.setLastSeenAt(lastSeen);
                                agentRepository.save(agent);
                                processed.incrementAndGet();
                            }
                        }
                    });
                }
                page++;
            }

            if (nextSyncToken != null) {
                syncTokenStore.saveToken("POSITION_SYNC", nextSyncToken);
                syncLog.setSyncToken(nextSyncToken);
            }

            syncLog.setStatus(SyncStatus.SUCCESS);
            syncLog.setRecordsProcessed(processed.get());
        } catch (Exception e) {
            log.error("Erro ao sincronizar localizações: {}", e.getMessage());
            syncLog.setStatus(SyncStatus.FAILED);
            syncLog.setErrorMessage(e.getMessage());
            throw new IllegalStateException("Erro ao sincronizar localizações.", e);
        } finally {
            syncLog.setFinishedAt(LocalDateTime.now());
            syncLogRepository.save(syncLog);
        }
    }

    public void syncCheckIns() {
        SyncLog syncLog = new SyncLog();
        syncLog.setStatus(SyncStatus.RUNNING);
        syncLog.setSyncType("CHECKIN_SYNC");
        syncLogRepository.save(syncLog);

        try {
            AtomicInteger processed = new AtomicInteger();
            String syncToken = syncTokenStore.getToken("CHECKIN_SYNC");
            String nextCursor = null;
            String nextSyncToken = syncToken;

            do {
                ExternalCheckInPageResponse response = gpsApiClient.getCheckIns(syncToken, nextCursor);
                if (response == null) {
                    break;
                }
                nextSyncToken = response.syncToken() != null ? response.syncToken() : nextSyncToken;

                for (ExternalCheckIn external : safeList(response.data())) {
                    String eventId = external.externalEventId() != null ? external.externalEventId() : external.id();
                    agentRepository.findByExternalId(external.agentId()).ifPresent(agent -> {
                        boolean exists = eventId != null && checkInRepository.existsByExternalEventId(eventId);
                        if (!exists && hasUsableGps(external.latitude(), external.longitude(), external.accuracy())) {
                            CheckIn checkIn = new CheckIn();
                            checkIn.setAgent(agent);
                            checkIn.setType(com.tracking.backend.checkin.mapper.CheckInTypeMapper.fromExternal(external.type()));
                            checkIn.setSource(external.source() != null ? external.source() : "EXTERNAL");
                            checkIn.setLat(external.latitude());
                            checkIn.setLng(external.longitude());
                            checkIn.setAddress(external.address());
                            checkIn.setAccuracy(external.accuracy());
                            checkIn.setSpeed(external.speed());
                            checkIn.setNotes(external.notes());
                            checkIn.setDistanceFromPrevious(external.distanceFromPrevious());
                            checkIn.setExternalEventId(eventId);
                            if (external.occurredAt() != null) {
                                checkIn.setOccurredAt(OffsetDateTime.parse(external.occurredAt()).toLocalDateTime());
                                checkIn.setCheckedInAt(checkIn.getOccurredAt());
                            }
                            if (external.syncedAt() != null) {
                                checkIn.setSyncedAt(OffsetDateTime.parse(external.syncedAt()).toLocalDateTime());
                            }
                            checkInRepository.save(checkIn);
                            processed.incrementAndGet();
                        }
                    });
                }

                String previousCursor = nextCursor;
                nextCursor = response.cursor();
                if (nextCursor != null && nextCursor.equals(previousCursor)) {
                    break;
                }
            } while (nextCursor != null);

            if (nextSyncToken != null) {
                syncTokenStore.saveToken("CHECKIN_SYNC", nextSyncToken);
                syncLog.setSyncToken(nextSyncToken);
            }

            syncLog.setStatus(SyncStatus.SUCCESS);
            syncLog.setRecordsProcessed(processed.get());
        } catch (Exception e) {
            log.error("Erro ao sincronizar check-ins: {}", e.getMessage());
            syncLog.setStatus(SyncStatus.FAILED);
            syncLog.setErrorMessage(e.getMessage());
            throw new IllegalStateException("Erro ao sincronizar check-ins.", e);
        } finally {
            syncLog.setFinishedAt(LocalDateTime.now());
            syncLogRepository.save(syncLog);
        }
    }


    private static <T> List<T> safeList(List<T> values) {
        return values != null ? values : List.of();
    }

    private static boolean hasUsableGps(Double lat, Double lng, Double accuracy) {
        return lat != null && lng != null && (accuracy == null || accuracy <= 50);
    }
}
