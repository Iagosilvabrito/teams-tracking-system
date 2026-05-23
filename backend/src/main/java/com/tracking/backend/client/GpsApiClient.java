package com.tracking.backend.client;

import com.tracking.backend.client.dto.AgentLocationPageResponse;
import com.tracking.backend.client.dto.AgentPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class GpsApiClient {

    private final WebClient webClient;

    @Value("${gps-api.api-key}")
    private String apiKey;

    public AgentPageResponse getAgents(int page, String syncToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/agents")
                        .queryParam("page", page)
                        .queryParamIfPresent("syncToken", java.util.Optional.ofNullable(syncToken))
                        .build())
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(AgentPageResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(ex -> ex.getMessage().contains("503")))
                .block();
    }

    public AgentLocationPageResponse getLocations(int page, String syncToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/locations")
                        .queryParam("page", page)
                        .queryParamIfPresent("syncToken", java.util.Optional.ofNullable(syncToken))
                        .build())
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(AgentLocationPageResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(ex -> ex.getMessage().contains("503")))
                .block();
    }
}