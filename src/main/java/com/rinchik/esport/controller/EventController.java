package com.rinchik.esport.controller;

import com.rinchik.esport.dto.attendance.AttendanceInfoResponse;
import com.rinchik.esport.dto.event.EventChangesRequest;
import com.rinchik.esport.dto.event.EventCreatingRequest;
import com.rinchik.esport.dto.event.EventInfoResponse;
import com.rinchik.esport.dto.event.TrainingInfoResponse;
import com.rinchik.esport.dto.user.UserInfoResponse;
import com.rinchik.esport.dto.user.UserShortInfoResponse;
import com.rinchik.esport.mapper.AttendanceMapper;
import com.rinchik.esport.mapper.EventMapper;
import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.TrainingAttendance;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.EventType;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.service.AttendanceService;
import com.rinchik.esport.service.EventService;
import com.rinchik.esport.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final UserService userService;
    private final AttendanceService attService;
    private final EventMapper mapper;
    private final AttendanceMapper attMapper;


    @GetMapping("/all_available")
    public ResponseEntity<List<EventInfoResponse>> getAll(@AuthenticationPrincipal UserDetails details,
                                                          @RequestParam(required = false) Integer month,
                                                          @RequestParam(required = false) Integer year) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        if (userService.getCurrentUser(details).getRoles().contains(SystemRole.ROLE_PLAYER)) {
            List<Event> events1;
            if (year == null)
                if (month == null)
                    events1 = eventService.findEventsByTeam(userService.getCurrentUser(details).getTeam().getId());
                else
                    events1 = eventService.findEventsByTeam(userService.getCurrentUser(details).getTeam().getId(), month,
                            LocalDateTime.now().getYear());
            else
                events1 = eventService.findEventsByTeam(userService.getCurrentUser(details).getTeam().getId(), month, year);
            for (var e : events1)
                dtos.add(mapper.toEventInfoResponse(e));
        }
        List<Event> events2;
        if (year == null)
            if (month == null)
                events2 = eventService.findCommonEvents();
            else
                events2 = eventService.findCommonEvents(month, LocalDateTime.now().getYear());
        else
            events2 = eventService.findCommonEvents(month, year);
        for (var e : events2)
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/team_events")
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public ResponseEntity<List<EventInfoResponse>> getEventsByTeam(@AuthenticationPrincipal UserDetails details,
                                                                   @RequestParam(required = false) Integer month,
                                                                   @RequestParam(required = false) Integer year) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        List<Event> events;
        if (year == null)
            if (month == null)
                events = eventService.findEventsByTeam(userService.getCurrentUser(details).getTeam().getId());
            else
                events = eventService.findEventsByTeam(userService.getCurrentUser(details).getTeam().getId(),
                        month, LocalDateTime.now().getYear());
        else
            events = eventService.findEventsByTeam(userService.getCurrentUser(details).getTeam().getId(), month, year);
        for (var e : events)
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventInfoResponse> getEventById(@AuthenticationPrincipal UserDetails details,
                                                          @PathVariable Long eventId) {
        User user = userService.getCurrentUser(details);
        Event event;
        if (user.getRoles().contains(SystemRole.ROLE_ADMIN))
            event = eventService.findEventById(eventId);
        else
            event = eventService.findEventByIdIfAvailable(eventId, user);
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toEventInfoResponse(event));
    }

    @PostMapping("/new_team_event")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<EventInfoResponse> createNewTeamEvent(@AuthenticationPrincipal UserDetails details,
                                                                @Valid @RequestBody EventCreatingRequest dto) {
        User user = userService.getCurrentUser(details);
        Event event = eventService.createNewEvent(user.getId(), user.getTeam().getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toEventInfoResponse(event));
    }

    @PostMapping("/new_common_event")
    public ResponseEntity<EventInfoResponse> createNewCommonEvent(@AuthenticationPrincipal UserDetails details,
                                                                  @Valid @RequestBody EventCreatingRequest dto) {
        User user = userService.getCurrentUser(details);
        Event event = eventService.createNewEvent(user.getId(), null, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toEventInfoResponse(event));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteTeamEvent(@AuthenticationPrincipal UserDetails details,
                                                @PathVariable Long eventId) {
        User user = userService.getCurrentUser(details);
        if (user.getRoles().contains(SystemRole.ROLE_CAPTAIN))
            eventService.deleteTeamEvent(eventId, user.getId());
        else if (user.getRoles().contains(SystemRole.ROLE_ADMIN))
            eventService.deleteEvent(eventId);
        else
            eventService.deleteEventByOrganizer(eventId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventInfoResponse> updateEvent(@AuthenticationPrincipal UserDetails details,
                                                         @PathVariable Long eventId,
                                                         @Valid @RequestBody EventChangesRequest dto) {
        User user = userService.getCurrentUser(details);
        Event event;
        if (user.getRoles().contains(SystemRole.ROLE_CAPTAIN))
            event = eventService.updateTeamEvent(eventId, dto, user.getId());
        else
            event = eventService.updateEventByOrganizer(eventId, dto, user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toEventInfoResponse(event));
    }

    @GetMapping("/events_organized_by_me")
    public ResponseEntity<List<EventInfoResponse>> getEventsByOrganizer(@AuthenticationPrincipal UserDetails details) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.getEventsByOrganizer(userService.getCurrentUser(details).getId()))
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/participating")
    public ResponseEntity<List<EventInfoResponse>> getEventsByParticipant(@AuthenticationPrincipal UserDetails details) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.getEventsByParticipant(userService.getCurrentUser(details).getId()))
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/common_events")
    public ResponseEntity<List<EventInfoResponse>> getCommonEvents(@AuthenticationPrincipal UserDetails details,
                                                                   @RequestParam(required = false) Integer month,
                                                                   @RequestParam(required = false) Integer year) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        List<Event> events;
        if (year == null)
            if (month == null)
                events = eventService.findCommonEvents();
            else
                events = eventService.findCommonEvents(month, LocalDateTime.now().getYear());
        else
            events = eventService.findCommonEvents(month, year);
        for (var e : events)
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/event_types")
    public ResponseEntity<List<EventType>> getAllEventTypes() {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEventTypes());
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<UserShortInfoResponse>> getParticipantsFromEvent(@AuthenticationPrincipal UserDetails details,
                                                                                @PathVariable Long eventId) {
        List<UserShortInfoResponse> dtos = new ArrayList<>();
        User user = userService.getCurrentUser(details);
        List<User> participants = eventService.findParticipantsFromEvent(eventId, user);
        for (User u : participants)
            dtos.add(mapper.toEventParticipantResponse(u));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @PostMapping("/{eventId}/participate")
    public ResponseEntity<Void> participateInEvent(@AuthenticationPrincipal UserDetails details,
                                                   @PathVariable Long eventId) {
        User user = userService.getCurrentUser(details);
        eventService.addParticipantToEvent(eventId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<Void> deleteParticipantFromEvent(@AuthenticationPrincipal UserDetails details,
                                                           @PathVariable Long eventId,
                                                           @PathVariable Long userId) {
        User user = userService.getCurrentUser(details);
        eventService.deleteParticipantFromEventByOrganizer(eventId, userId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/trainings")
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public ResponseEntity<List<TrainingInfoResponse>> getAllTeamTrainings(@AuthenticationPrincipal UserDetails details) {
        List<Event> trainings = eventService.findAllTrainingsByTeam(userService.getCurrentUser(details).getTeam().getId());
        List<TrainingInfoResponse> dtos = new ArrayList<>();
        for (Event e : trainings)
            dtos.add(mapper.toTrainingInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/trainings/{eventId}/attendance")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<List<AttendanceInfoResponse>> getTrainingAttendance(@AuthenticationPrincipal UserDetails details,
                                                                              @PathVariable Long eventId) {
        List<TrainingAttendance> atts = attService.findTrainingAttendanceByCaptain(eventId,
                userService.getCurrentUser(details));
        List<AttendanceInfoResponse> dtos = new ArrayList<>();
        for (TrainingAttendance a : atts)
            dtos.add(attMapper.toAttendanceInfoResponse(a));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }
}
