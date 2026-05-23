package com.tracking.backend.checkin.controller;

import com.tracking.backend.checkin.dto.CheckInRequest;
import com.tracking.backend.checkin.dto.CheckInResponse;
import com.tracking.backend.checkin.service.CheckInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping("/{id}/check-ins")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckInResponse checkIn(@PathVariable Long id, @RequestBody @Valid CheckInRequest request) {
        return checkInService.checkIn(id, request);
    }

    @GetMapping("/{id}/check-ins")
    public List<CheckInResponse> findCheckIns(@PathVariable Long id) {
        return checkInService.findByAgent(id);
    }
}