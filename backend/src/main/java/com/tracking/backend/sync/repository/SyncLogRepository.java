package com.tracking.backend.sync.repository;

import com.tracking.backend.sync.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {
    List<SyncLog> findTop20ByOrderByStartedAtDesc();
    List<SyncLog> findBySyncTypeOrderByStartedAtDesc(String syncType);
}