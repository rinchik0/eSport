package com.rinchik.esport.service;

import com.rinchik.esport.dto.event.EventCreatingDto;
import com.rinchik.esport.dto.event.EventDetailsDto;
import com.rinchik.esport.dto.event.EventInfoWithParticipantsDto;
import com.rinchik.esport.exception.*;
import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.Game;
import com.rinchik.esport.repository.EventRepository;
import com.rinchik.esport.repository.TeamRepository;
import com.rinchik.esport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository eventRepo;
    private final UserRepository userRepo;
    private final TeamRepository teamRepo;

    public List<Event> findAllEvents() {
        return eventRepo.findAll();
    }

    public List<EventDetailsDto> findAllEventsDto() {
        List<Event> events = eventRepo.findAll();
        List<EventDetailsDto> dtos = new ArrayList<>();
        for (var e : events) {
            EventDetailsDto dto = new EventDetailsDto();
            dto.setName(e.getName());
            dto.setDescription(e.getDescription());
            dto.setType(e.getType());
            dto.setDate(e.getDate());
            dto.setOrganizerName(e.getOrganizer().getLogin());
            dtos.add(dto);
        }
        return dtos;
    }

    public List<Event> findEventsByGame(Game game) {
        return eventRepo.findByGame(game);
    }

    public Event findEventById(Long id) {
        return eventRepo.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    public EventInfoWithParticipantsDto findEventDtoById(Long id) {
        Event event = eventRepo.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        EventInfoWithParticipantsDto dto = new EventInfoWithParticipantsDto();
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setType(event.getType());
        dto.setOrganizerName(event.getOrganizer().getLogin());
        dto.setTeamName(event.getTeam().getName() != null ? event.getTeam().getName() : null);
        List<String> part = new ArrayList<>();
        for (var p : event.getParticipants())
            part.add(p.getLogin());
        dto.setParticipantNames(part);
        return dto;
    }

    @Transactional
    public Event createNewEvent(EventCreatingDto dto) {
        Event newEvent = new Event();

        newEvent.setName(dto.getName());
        newEvent.setDescription(dto.getDescription());
        newEvent.setType(dto.getType());
        newEvent.setDate(dto.getDate());

        newEvent.setOrganizer(userRepo.findById(dto.getOrganizerId())
                .orElseThrow(() -> new UserNotFoundException(dto.getOrganizerId())));
        newEvent.setTeam(teamRepo.findById(dto.getTeamId())
                .orElseThrow(() -> null));

        return eventRepo.save(newEvent);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepo.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        event.setTeam(null);
        event.setOrganizer(null);
        event.setParticipants(null);

        eventRepo.delete(event);
    }

    public List<User> findParticipantsByEvent(Long id) {
        Event event = eventRepo.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        return event.getParticipants();
    }

    @Transactional
    public void addParticipantToEvent(Long eventId, Long userId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (event.getParticipants().contains(user))
            throw new UserAlreadyEventParticipantException(userId, eventId);

        event.getParticipants().add(user);
    }

    @Transactional
    public void deleteParticipantFromEvent(Long eventId, Long userId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!event.getParticipants().contains(user))
            throw new UserNotEventParticipantException(userId, eventId);

        event.getParticipants().remove(user);
    }
}
