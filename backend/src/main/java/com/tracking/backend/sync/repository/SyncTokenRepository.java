package com.tracking.backend.sync.repository;

import com.tracking.backend.sync.entity.SyncToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SyncTokenRepository extends JpaRepository<SyncToken, Long> {
    Optional<SyncToken> findBySyncType(String syncType);
}