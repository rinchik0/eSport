package com.rinchik.esport.mapper;

import com.rinchik.esport.dto.event.EventInfoResponse;
import com.rinchik.esport.dto.event.TrainingInfoResponse;
import com.rinchik.esport.dto.user.UserShortInfoResponse;
import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.EventStatus;
import com.rinchik.esport.model.enums.EventType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class EventMapper {
    public EventInfoResponse toEventInfoResponse(Event event) {
        EventInfoResponse dto = new EventInfoResponse();
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setId(event.getId());
        dto.setTeamId(event.getTeam().getId());
        dto.setTeamName(event.getTeam().getName());
        dto.setDate(event.getDate());
        dto.setType(event.getType());
        dto.setOrganizerId(event.getOrganizer().getId());
        dto.setOrganizerName(event.getOrganizer().getUsername());
        dto.setMaxAmountOfParticipants(event.getMaxAmountOfParticipants());
        dto.setAmountOfParticipants(event.getParticipants().size());
        dto.setStatus(event.getStatus());
        dto.setPrize(event.getPrize());
        return dto;
    }

    public UserShortInfoResponse toEventParticipantResponse(User user) {
        UserShortInfoResponse dto = new UserShortInfoResponse();
        dto.setUserId(user.getId());
        dto.setUserName(dto.getUserName());
        return dto;
    }

    public TrainingInfoResponse toTrainingInfoResponse(Event event) {
        TrainingInfoResponse dto = new TrainingInfoResponse();
        dto.setEventId(event.getId());
        dto.setDate(event.getDate());
        dto.setTitle(event.getTitle());
        return dto;
    }
}
