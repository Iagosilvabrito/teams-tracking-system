package com.tracking.backend.location.controller;

import com.tracking.backend.location.dto.RouteHistoryResponse;
import com.tracking.backend.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/{id}/route")
    public RouteHistoryResponse getRouteHistory(@PathVariable Long id) {
        return locationService.getRouteHistory(id);
    }
}