package com.tracking.backend.sync.scheduler;

import com.tracking.backend.sync.entity.SyncLog;
import com.tracking.backend.sync.entity.SyncStatus;
import com.tracking.backend.sync.repository.SyncLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncMonitorScheduler {

    private final SyncLogRepository syncLogRepository;

    @Scheduled(fixedRate = 600000)
    public void monitor() {
        log.info("Verificando saúde das sincronizações...");
        List<SyncLog> recentLogs = syncLogRepository.findTop20ByOrderByStartedAtDesc();

        long failures = recentLogs.stream()
                .filter(log -> log.getStatus() == SyncStatus.FAILED)
                .count();

        if (failures > 0) {
            log.warn("Atenção: {} sincronizações falharam recentemente!", failures);
        } else {
            log.info("Todas as sincronizações estão saudáveis!");
        }
    }
}