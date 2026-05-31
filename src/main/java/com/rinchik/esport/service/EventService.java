package com.rinchik.esport.service;

import com.rinchik.esport.dto.event.EventChangesRequest;
import com.rinchik.esport.dto.event.EventCreatingRequest;
import com.rinchik.esport.exception.*;
import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.EventType;
import com.rinchik.esport.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public Event findEventById(Long id) {
        return eventRepo.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    public Event findEventByIdIfAvailable(Long eventId, User user) {
        Event event = findEventById(eventId);
        if (event.getTeam() == null || event.getTeam().getId().equals(user.getTeam().getId()))
            return event;
        throw new UserNotTeamMemberException(user.getId(), event.getTeam().getId());
    }

    public List<Event> findEventsByTeam(Long teamId) {
        return eventRepo.findByTeam(teamService.findTeamById(teamId));
    }

    private List<Event> filterByMonth(List<Event> events, int month, int year) {
        List<Event> result = new ArrayList<>();
        LocalDateTime begin = LocalDateTime.of(
                LocalDate.of(year, month, 1),
                LocalTime.of(0, 0, 0)
        );
        LocalDateTime end = LocalDateTime.of(
                LocalDate.of(year + (month == 12 ? 1 : 0), month == 12 ? 1 : month + 1, 1),
                LocalTime.of(0, 0, 0)
        );
        for (Event e : events)
            if (e.getDate().isAfter(begin) && e.getDate().isBefore(end))
                result.add(e);
        return result;
    }

    public List<Event> findEventsByTeam(Long teamId, int month, int year) {
        return filterByMonth(eventRepo.findByTeam(teamService.findTeamById(teamId)), month, year);
    }

    public List<Event> findCommonEvents() {
        return eventRepo.findByTeamIsNull();
    }

    public List<Event> findCommonEvents(Integer month, Integer year) {
        return filterByMonth(eventRepo.findByTeamIsNull(), month, year);
    }

    @Transactional
    public Event createNewEvent(Long organizerId, Long teamId, EventCreatingRequest dto) {
        Event newEvent = new Event();

        newEvent.setTitle(dto.getTitle());
        newEvent.setDescription(dto.getDescription() == null ? null : dto.getDescription());
        newEvent.setType(dto.getType());
        newEvent.setDate(dto.getDate());
        newEvent.setPrize(dto.getPrize() == null ? null : dto.getPrize());
        newEvent.setMaxAmountOfParticipants(
                dto.getMaxAmountOfParticipants() != null ? dto.getMaxAmountOfParticipants() : 100);

        newEvent.setOrganizer(userService.findUserById(organizerId));
        newEvent.setTeam(teamService.findTeamById(teamId));
        newEvent.setParticipants(new ArrayList<>());

        return eventRepo.save(newEvent);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = findEventById(id);

        event.setTeam(null);
        event.setOrganizer(null);

        eventRepo.delete(event);
    }

    public void checkIfUserOrganizerOfEvent(Long userId, Event event) {
        if (!event.getOrganizer().getId().equals(userId))
            throw new UserNotEventOrganizerException(userId, event.getId());
    }

    @Transactional
    public void deleteEventByOrganizer(Long eventId, Long userId) {
        Event event = findEventById(eventId);
        checkIfUserOrganizerOfEvent(userId, event);
        deleteEvent(eventId);
    }

    @Transactional
    public void deleteTeamEvent(Long eventId, Long userId) {
        Event event = findEventById(eventId);
        User user = userService.findUserById(userId);
        if (!event.getTeam().getId().equals(user.getTeam().getId()))
            throw new UserNotTeamMemberException(userId, event.getTeam().getId());
        deleteEvent(eventId);
    }

    public List<Event> getEventsByOrganizer(Long userId) {
        return eventRepo.findByOrganizer(userService.findUserById(userId));
    }

    public List<EventType> getAllEventTypes() {
        return EventType.getAll();
    }

    @Transactional
    public Event updateEvent(Event event, EventChangesRequest dto) {
        if (dto.getTitle() != null)
            event.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            event.setDescription(dto.getDescription());
        if (dto.getPrize() != null)
            event.setPrize(dto.getPrize());
        if (dto.getDate() != null)
            event.setDate(dto.getDate());
        if (dto.getMaxAmountOfParticipants() != null)
            event.setMaxAmountOfParticipants(dto.getMaxAmountOfParticipants());
        return event;
    }

    @Transactional
    public Event updateTeamEvent(Long eventId, EventChangesRequest dto, Long userId) {
        Event event = findEventById(eventId);
        User user = userService.findUserById(userId);
        if (!event.getTeam().getId().equals(user.getTeam().getId()))
            throw new UserNotTeamMemberException(userId, event.getTeam().getId());
        return updateEvent(event, dto);
    }

    @Transactional
    public Event updateEventByOrganizer(Long eventId, EventChangesRequest dto, Long userId) {
        Event event = findEventById(eventId);
        checkIfUserOrganizerOfEvent(userId, event);
        return updateEvent(event, dto);
    }

    public List<Event> getEventsByParticipant(Long userId) {
        List<Event> events = new ArrayList<>();
        User user = userService.findUserById(userId);
        for (Event e : findAllEvents())
            if (e.getParticipants().contains(user))
                events.add(e);
        return events;
    }

    public List<User> findParticipantsFromEvent(Long eventId, User user) {
        Event event = findEventByIdIfAvailable(eventId, user);
        return event.getParticipants();
    }

    @Transactional
    public void addParticipantToEvent(Long eventId, User user) {
        Event event = findEventByIdIfAvailable(eventId, user);
        event.getParticipants().add(user);
    }

    @Transactional
    public void deleteParticipantFromEventByOrganizer(Long eventId, Long userId, User organizer) {
        Event event = findEventByIdIfAvailable(eventId, organizer);
        checkIfUserOrganizerOfEvent(organizer.getId(), event);
        event.getParticipants().remove(userService.findUserById(userId));
    }

    public List<Event> findAllTrainingsByTeam(Long teamId) {
        List<Event> events = findEventsByTeam(teamId);
        List<Event> trainings = new ArrayList<>();
        for (Event e : events)
            if (e.getType() == EventType.TRAINING)
                trainings.add(e);
        return trainings;
    }

    public List<Event> findEventsByParticipant(Long userId) {
        return eventRepo.findByParticipantsId(userId);
    }

    public List<Event> findTournamentsByParticipant(Long userId) {
       List<Event> events = findEventsByParticipant(userId);
       List<Event> tournaments = new ArrayList<>();
       for (Event e : events)
           if (e.getType() == EventType.TOURNAMENT)
               tournaments.add(e);
       return tournaments;
    }
}
