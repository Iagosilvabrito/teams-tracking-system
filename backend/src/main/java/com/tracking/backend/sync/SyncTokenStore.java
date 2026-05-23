package com.tracking.backend.sync;

import com.tracking.backend.sync.entity.SyncToken;
import com.tracking.backend.sync.repository.SyncTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SyncTokenStore {

    private final SyncTokenRepository syncTokenRepository;

    public String getToken(String syncType) {
        return syncTokenRepository.findBySyncType(syncType)
                .map(SyncToken::getToken)
                .orElse(null);
    }

    public void saveToken(String syncType, String token) {
        SyncToken syncToken = syncTokenRepository.findBySyncType(syncType)
                .orElse(new SyncToken());
        syncToken.setSyncType(syncType);
        syncToken.setToken(token);
        syncTokenRepository.save(syncToken);
    }
}