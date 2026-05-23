package com.tracking.backend.checkin.mapper;

import com.tracking.backend.agent.entity.Agent;
import com.tracking.backend.checkin.dto.CheckInRequest;
import com.tracking.backend.checkin.dto.CheckInResponse;
import com.tracking.backend.checkin.entity.CheckIn;

public class CheckInMapper {

    public static CheckIn toEntity(CheckInRequest request, Agent agent) {
        CheckIn checkIn = new CheckIn();
        checkIn.setAgent(agent);
        checkIn.setLat(request.lat());
        checkIn.setLng(request.lng());
        checkIn.setAddress(request.address());
        checkIn.setNotes(request.notes());
        return checkIn;
    }

    public static CheckInResponse toResponse(CheckIn checkIn) {
        return new CheckInResponse(
                checkIn.getId(),
                checkIn.getAgent().getId(),
                checkIn.getAgent().getName(),
                checkIn.getLat(),
                checkIn.getLng(),
                checkIn.getAddress(),
                checkIn.getNotes(),
                checkIn.getCheckedInAt()
        );
    }
}