package com.tracking.backend.sync.scheduler;

import com.tracking.backend.location.entity.Location;
import com.tracking.backend.location.repository.LocationRepository;
import com.tracking.backend.agent.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteHistoryScheduler {

    private final AgentRepository agentRepository;
    private final LocationRepository locationRepository;

    @Scheduled(fixedRate = 60000)
    public void consolidate() {
        log.info("Consolidando histórico de rotas do dia...");
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        agentRepository.findAll().forEach(agent -> {
            List<Location> locations = locationRepository
                    .findByAgentIdAndRecordedAtBetweenOrderByRecordedAtAsc(
                            agent.getId(), startOfDay, now
                    );
            log.info("Agente {} possui {} registros de rota hoje", agent.getName(), locations.size());
        });
        log.info("Consolidação de rotas concluída!");
    }
}