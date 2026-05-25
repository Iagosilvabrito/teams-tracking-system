package com.tracking.backend.checkin.controller;

import com.tracking.backend.checkin.dto.CheckInRequest;
import com.tracking.backend.checkin.dto.CheckInResponse;
import com.tracking.backend.checkin.service.CheckInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @GetMapping("/api/v1/check-ins")
    public Map<String, Object> findAll(
            @RequestParam(required = false) Long agentId,
            @RequestParam(required = false) String type) {
        return Map.of("data", checkInService.findAll(agentId, type));
    }

    @PostMapping("/api/agents/{id}/check-ins")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckInResponse checkIn(@PathVariable Long id, @RequestBody @Valid CheckInRequest request) {
        return checkInService.checkIn(id, request);
    }

    @GetMapping("/api/agents/{id}/check-ins")
    public List<CheckInResponse> findCheckIns(@PathVariable Long id) {
        return checkInService.findByAgent(id);
    }
}