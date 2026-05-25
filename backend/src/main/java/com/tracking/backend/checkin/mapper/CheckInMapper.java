package com.tracking.backend.checkin.mapper;

import com.tracking.backend.agent.entity.Agent;
import com.tracking.backend.checkin.dto.CheckInRequest;
import com.tracking.backend.checkin.dto.CheckInResponse;
import com.tracking.backend.checkin.entity.CheckIn;
import com.tracking.backend.checkin.entity.CheckInType;

public class CheckInMapper {

    public static CheckIn toEntity(CheckInRequest request, Agent agent) {
        CheckIn checkIn = new CheckIn();
        checkIn.setAgent(agent);
        checkIn.setType(CheckInType.CHECKIN);
        checkIn.setSource("MANUAL");
        checkIn.setLat(request.lat());
        checkIn.setLng(request.lng());
        checkIn.setAddress(request.address());
        checkIn.setNotes(request.notes());
        return checkIn;
    }

    public static CheckInResponse toResponse(CheckIn checkIn) {
        return new CheckInResponse(
                String.valueOf(checkIn.getId()),
                String.valueOf(checkIn.getAgent().getId()),
                checkIn.getType() != null ? checkIn.getType().name() : null,
                checkIn.getSource(),
                checkIn.getLat(),
                checkIn.getLng(),
                checkIn.getAddress(),
                checkIn.getAccuracy(),
                checkIn.getSpeed(),
                checkIn.getNotes(),
                checkIn.getDistanceFromPrevious(),
                checkIn.getExternalEventId(),
                checkIn.getOccurredAt(),
                checkIn.getSyncedAt()
        );
    }
}