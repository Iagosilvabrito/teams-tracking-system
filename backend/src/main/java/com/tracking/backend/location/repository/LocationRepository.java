package com.tracking.backend.location.repository;

import com.tracking.backend.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByAgentIdAndRecordedAtBetweenOrderByRecordedAtAsc(
            Long agentId, LocalDateTime start, LocalDateTime end
    );
    boolean existsByAgentIdAndRecordedAt(Long agentId, LocalDateTime recordedAt);
}