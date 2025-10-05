package com.rinchik.esport.mapper;

import com.rinchik.esport.dto.event.EventInfoResponse;
import com.rinchik.esport.model.Event;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class EventMapper {
    public EventInfoResponse toEventInfoResponse(Event event) {
        EventInfoResponse dto = new EventInfoResponse();
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setParticipantNames(new ArrayList<>());
        for (var p :event.getParticipants())
            dto.getParticipantNames().add(p.getLogin());
        dto.setTeamName(event.getTeam().getName());
        dto.setDate(event.getDate());
        dto.setType(event.getType());
        dto.setOrganizerName(event.getOrganizer().getLogin());
        return dto;
    }
}
