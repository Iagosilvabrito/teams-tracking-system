package com.tracking.backend.client;

import com.tracking.backend.client.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GpsApiClient {

    private final WebClient webClient;

    @Value("${gps-api.api-key}")
    private String apiKey;

    public AgentPageResponse getAgents(int page, String syncToken) {
        return executeWithRetry(() -> webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/agents")
                        .queryParam("page", page)
                        .queryParam("limit", 50)
                        .queryParamIfPresent("syncToken", Optional.ofNullable(syncToken))
                        .build())
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(AgentPageResponse.class)
                .block());
    }

    public AgentLocationPageResponse getLocations(int page, String syncToken) {
        return executeWithRetry(() -> webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/locations")
                        .queryParam("page", page)
                        .queryParam("limit", 50)
                        .queryParamIfPresent("syncToken", Optional.ofNullable(syncToken))
                        .build())
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(AgentLocationPageResponse.class)
                .block());
    }

    public ExternalCheckInPageResponse getCheckIns(String syncToken, String cursor) {
        return executeWithRetry(() -> webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/agents/events")
                        .queryParam("limit", 50)
                        .queryParamIfPresent("cursor", Optional.ofNullable(cursor))
                        .queryParamIfPresent("syncToken", Optional.ofNullable(syncToken))
                        .build())
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(ExternalCheckInPageResponse.class)
                .block());
    }

    private <T> T executeWithRetry(Supplier<T> request) {
        int maxAttempts = 4;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return request.get();
            } catch (WebClientResponseException ex) {
                int status = ex.getStatusCode().value();
                if (attempt == maxAttempts || (status != 429 && status != 503)) {
                    throw ex;
                }

                Duration wait = status == 429
                        ? retryAfterDelay(ex)
                        : backoffWithJitter(attempt);

                log.warn("API externa retornou {}. Tentativa {}/{}. Aguardando {} ms.",
                        status, attempt, maxAttempts, wait.toMillis());
                sleep(wait);
            }
        }
        throw new IllegalStateException("Falha ao executar requisição na API externa.");
    }

    private Duration retryAfterDelay(WebClientResponseException ex) {
        String retryAfter = ex.getHeaders().getFirst("Retry-After");
        if (retryAfter == null || retryAfter.isBlank()) {
            return Duration.ofSeconds(5);
        }

        try {
            return Duration.ofSeconds(Long.parseLong(retryAfter));
        } catch (NumberFormatException ignored) {
            try {
                ZonedDateTime retryAt = ZonedDateTime.parse(retryAfter, DateTimeFormatter.RFC_1123_DATE_TIME);
                Duration delay = Duration.between(ZonedDateTime.now(retryAt.getZone()), retryAt);
                return delay.isNegative() ? Duration.ZERO : delay;
            } catch (Exception ignoredDateParse) {
                return Duration.ofSeconds(5);
            }
        }
    }

    private Duration backoffWithJitter(int attempt) {
        long baseMillis = (long) (1000 * Math.pow(2, attempt - 1));
        long jitterMillis = ThreadLocalRandom.current().nextLong(250, 1000);
        return Duration.ofMillis(baseMillis + jitterMillis);
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry interrompido.", e);
        }
    }
}
