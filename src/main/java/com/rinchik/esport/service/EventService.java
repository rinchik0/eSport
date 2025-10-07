package com.rinchik.esport.service;

import com.rinchik.esport.dto.event.EventCreatingRequest;
import com.rinchik.esport.exception.*;
import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.EventType;
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

    private boolean isUserOrganizerForEvent(Long userId, Long eventId) {
        Event event = findEventById(eventId);
        return userId.equals(event.getOrganizer().getId());
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = findEventById(id);

        event.setTeam(null);
        event.setOrganizer(null);
        event.setParticipants(null);

        eventRepo.delete(event);
    }

    @Transactional
    public void deleteEventByOrganizer(Long eventId, Long userId) {
        if (isUserOrganizerForEvent(userId, eventId))
            deleteEvent(eventId);
        else
            throw new NotEventOrganizerException(userId, eventId);
    }

    public List<User> findParticipantsByEvent(Long id) {
        Event event = findEventById(id);
        return event.getParticipants();
    }

    @Transactional
    public void addParticipantToEvent(Long eventId, Long userId) {
        Event event = findEventById(eventId);
        User user = userService.findUserById(userId);

        if (event.getParticipants().contains(user))
            throw new UserAlreadyEventParticipantException(userId, eventId);

        event.getParticipants().add(user);
    }

    @Transactional
    public void addParticipantToEventByOrganizer(Long eventId, Long userId, Long organizerId) {
        if (isUserOrganizerForEvent(organizerId, eventId))
            addParticipantToEvent(eventId, userId);
        else
            throw new NotEventOrganizerException(organizerId, eventId);
    }

    @Transactional
    public void deleteParticipantFromEvent(Long eventId, Long userId) {
        Event event = findEventById(eventId);
        User user = userService.findUserById(userId);

        if (!event.getParticipants().contains(user))
            throw new UserNotEventParticipantException(userId, eventId);

        event.getParticipants().remove(user);
    }

    @Transactional
    public void deleteParticipantFromEventByOrganizer(Long eventId, Long userId, Long organizerId) {
        if (isUserOrganizerForEvent(organizerId, eventId))
            deleteParticipantFromEvent(eventId, userId);
        else
            throw new NotEventOrganizerException(organizerId, eventId);
    }

    public List<Event> getEventsByOrganizer(Long userId) {
        return eventRepo.findByOrganizer(userService.findUserById(userId));
    }

    public List<Event> getEventsByParticipant(Long userId) {
        return eventRepo.findByParticipantsId(userId);
    }

    public List<EventType> getAllEventTypes() {
        return EventType.getAll();
    }
}
