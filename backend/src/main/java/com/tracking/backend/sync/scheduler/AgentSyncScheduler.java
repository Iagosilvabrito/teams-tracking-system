package com.tracking.backend.sync.scheduler;

import com.tracking.backend.sync.service.SyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgentSyncScheduler {

    private final SyncService syncService;

    @Scheduled(fixedRate = 300000)
    public void sync() {
        log.info("Iniciando sincronização de agentes...");
        syncService.syncAgents();
        log.info("Sincronização de agentes concluída!");
    }
}