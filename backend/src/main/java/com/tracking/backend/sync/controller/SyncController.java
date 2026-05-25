package com.tracking.backend.sync.controller;

import com.tracking.backend.sync.entity.SyncLog;
import com.tracking.backend.sync.repository.SyncLogRepository;
import com.tracking.backend.sync.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;
    private final SyncLogRepository syncLogRepository;

    @PostMapping("/agents")
    public Map<String, Object> syncAgents() {
        syncService.syncAgents();
        Map<String, Object> result = new HashMap<>();
        result.put("synced", getRecords("AGENT_SYNC"));
        return result;
    }

    @PostMapping("/locations")
    public Map<String, Object> syncLocations() {
        syncService.syncLocations();
        Map<String, Object> result = new HashMap<>();
        result.put("synced", getRecords("POSITION_SYNC"));
        return result;
    }

    @PostMapping("/check-ins")
    public Map<String, Object> syncCheckIns() {
        syncService.syncCheckIns();
        Map<String, Object> result = new HashMap<>();
        return syncLogRepository.findTopBySyncTypeOrderByFinishedAtDesc("CHECKIN_SYNC")
                .map(log -> {
                    result.put("synced", log.getRecordsProcessed() != null ? log.getRecordsProcessed() : 0);
                    result.put("syncToken", log.getSyncToken() != null ? log.getSyncToken() : "");
                    return result;
                })
                .orElseGet(() -> {
                    result.put("synced", 0);
                    result.put("syncToken", "");
                    return result;
                });
    }

    @PostMapping("/all")
    public Map<String, Object> syncAll() {
        CompletableFuture<Void> agents    = CompletableFuture.runAsync(syncService::syncAgents);
        CompletableFuture<Void> locations = CompletableFuture.runAsync(syncService::syncLocations);
        CompletableFuture<Void> checkIns  = CompletableFuture.runAsync(syncService::syncCheckIns);

        CompletableFuture.allOf(agents, locations, checkIns).join();

        Map<String, Object> result = new HashMap<>();
        result.put("agents",    getRecords("AGENT_SYNC"));
        result.put("locations", getRecords("POSITION_SYNC"));
        result.put("checkIns",  getRecords("CHECKIN_SYNC"));
        return result;
    }

    @GetMapping("/logs")
    public List<SyncLog> getLogs() {
        return syncLogRepository.findTop20ByOrderByStartedAtDesc();
    }

    @GetMapping("/logs/{syncType}")
    public List<SyncLog> getLogsBySyncType(@PathVariable String syncType) {
        return syncLogRepository.findBySyncTypeOrderByStartedAtDesc(syncType.toUpperCase());
    }

    private int getRecords(String syncType) {
        return syncLogRepository.findTopBySyncTypeOrderByFinishedAtDesc(syncType)
                .map(log -> log.getRecordsProcessed() != null ? log.getRecordsProcessed() : 0)
                .orElse(0);
    }
}
