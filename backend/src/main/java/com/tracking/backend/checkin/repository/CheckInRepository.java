package com.tracking.backend.checkin.repository;

import com.tracking.backend.checkin.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckIn,Long> {
    List<CheckIn> findByAgentIdOrderByCheckedInAtDesc(Long agentId);
}
