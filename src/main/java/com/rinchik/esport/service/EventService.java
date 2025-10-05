package com.rinchik.esport.service;

import com.rinchik.esport.dto.event.EventCreatingRequest;
import com.rinchik.esport.exception.*;
import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.Game;
import com.rinchik.esport.repository.EventRepository;
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
    private final UserService userService;
    private final TeamService teamService;

    public List<Event> findAllEvents() {
        return eventRepo.findAll();
    }

    public List<Event> findEventsByGame(Game game) {
        return eventRepo.findByGame(game);
    }

    public Event findEventById(Long id) {
        return eventRepo.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    public List<Event> findEventsByTeam(Long teamId) {
        return eventRepo.findByTeam(teamService.findTeamById(teamId));
    }

    public List<Event> findCommonEvents() {
        return eventRepo.findByTeamIsNull();
    }

    @Transactional
    public Event createNewEvent(Long organizerId, Long teamId, EventCreatingRequest dto) {
        Event newEvent = new Event();

        newEvent.setName(dto.getName());
        newEvent.setDescription(dto.getDescription() == null ? null : dto.getDescription());
        newEvent.setType(dto.getType());
        newEvent.setDate(dto.getDate());

        newEvent.setOrganizer(userService.findUserById(organizerId));
        newEvent.setTeam(teamService.findTeamById(teamId));

        newEvent.setParticipants(new ArrayList<>());
        newEvent.getParticipants().add(userService.findUserById(organizerId));

        return eventRepo.save(newEvent);
    }

    public boolean isUserOrganizerForEvent(Long userId, Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        return userId.equals(event.getOrganizer().getId());
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
        User user = userService.findUserById(userId);

        if (event.getParticipants().contains(user))
            throw new UserAlreadyEventParticipantException(userId, eventId);

        event.getParticipants().add(user);
    }

    @Transactional
    public void deleteParticipantFromEvent(Long eventId, Long userId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        User user = userService.findUserById(userId);

        if (!event.getParticipants().contains(user))
            throw new UserNotEventParticipantException(userId, eventId);

        event.getParticipants().remove(user);
    }

    public List<Event> getEventsByOrganizer(Long userId) {
        return eventRepo.findByOrganizer(userService.findUserById(userId));
    }

    public List<Event> getEventsByParticipant(Long userId) {
        return eventRepo.findByParticipantsId(userId);
    }
}
