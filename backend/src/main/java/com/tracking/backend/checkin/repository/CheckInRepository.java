package com.tracking.backend.checkin.repository;

import com.tracking.backend.checkin.entity.CheckIn;
import com.tracking.backend.checkin.entity.CheckInType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    List<CheckIn> findByAgentIdOrderByCheckedInAtDesc(Long agentId);
    List<CheckIn> findByAgentIdAndTypeOrderByOccurredAtDesc(Long agentId, CheckInType type);
    List<CheckIn> findByTypeOrderByOccurredAtDesc(CheckInType type);
    List<CheckIn> findAllByOrderByOccurredAtDesc();
    boolean existsByExternalEventId(String externalEventId);
    Optional<CheckIn> findTopByAgentIdOrderByCheckedInAtDesc(Long agentId);
    void deleteByAgentId(Long agentId);
}
