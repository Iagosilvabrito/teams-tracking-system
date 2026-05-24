package com.tracking.backend.client.mapper;

import com.tracking.backend.agent.entity.Agent;
import com.tracking.backend.client.dto.ExternalLocation;
import com.tracking.backend.location.entity.Location;
import java.time.LocalDateTime;

public class ExternalLocationMapper {

    public static Location toEntity(ExternalLocation external, Agent agent, LocalDateTime lastSeen) {
        Location location = new Location();
        location.setAgent(agent);
        location.setLat(external.latitude());
        location.setLng(external.longitude());
        location.setAccuracy(external.accuracy());
        location.setRecordedAt(lastSeen);
        return location;
    }
}