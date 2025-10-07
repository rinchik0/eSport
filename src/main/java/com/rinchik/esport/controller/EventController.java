package com.rinchik.esport.controller;

import com.rinchik.esport.dto.event.EventCreatingRequest;
import com.rinchik.esport.dto.event.EventInfoResponse;
import com.rinchik.esport.mapper.EventMapper;
import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.EventType;
import com.rinchik.esport.model.enums.Game;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final UserService userService;
    private final EventMapper mapper;

    @GetMapping("/all_available")
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public ResponseEntity<List<EventInfoResponse>> getAll(@AuthenticationPrincipal UserDetails details) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.findEventsByTeam(userService.getCurrentUser(details).getTeam().getId()))
            dtos.add(mapper.toEventInfoResponse(e));
        for (var e : eventService.findCommonEvents())
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/team_events")
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public ResponseEntity<List<EventInfoResponse>> getEventsByTeam(@AuthenticationPrincipal UserDetails details) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.findEventsByTeam(userService.getCurrentUser(details).getTeam().getId()))
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/game")
    public ResponseEntity<List<EventInfoResponse>> getEventsByGame(@RequestParam Game game) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.findEventsByGame(game))
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventInfoResponse> getEventById(@PathVariable Long eventId) {
        Event event = eventService.findEventById(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toEventInfoResponse(event));
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<EventInfoResponse> createNewTeamEvent(@AuthenticationPrincipal UserDetails details,
                                                                @Valid @RequestBody EventCreatingRequest dto) {
        User user = userService.getCurrentUser(details);
        Event event = eventService.createNewEvent(user.getId(), user.getTeam().getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toEventInfoResponse(event));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> deleteTeamEvent(@AuthenticationPrincipal UserDetails details,
                                                @PathVariable Long eventId) {
        User user = userService.getCurrentUser(details);
        eventService.deleteEventByOrganizer(eventId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<String>> getParticipantsOfEvent(@PathVariable Long eventId) {
        List<String> dtos = new ArrayList<>();
        for (var p : eventService.findParticipantsByEvent(eventId))
            dtos.add(p.getLogin());
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @PostMapping("/{eventId}/participants")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> addParticipantToEvent(@AuthenticationPrincipal UserDetails details,
                                                      @RequestParam Long userId,
                                                      @PathVariable Long eventId) {
        User user = userService.getCurrentUser(details);
        eventService.addParticipantToEventByOrganizer(eventId, userId, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{eventId}/participants")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> deleteParticipantFromEvent(@AuthenticationPrincipal UserDetails details,
                                                           @RequestParam Long userId,
                                                           @PathVariable Long eventId) {
        User user = userService.getCurrentUser(details);
        eventService.deleteParticipantFromEventByOrganizer(eventId, userId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/events_organized_by_me")
    public ResponseEntity<List<EventInfoResponse>> getEventsByOrganizer(@AuthenticationPrincipal UserDetails details) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.getEventsByOrganizer(userService.getCurrentUser(details).getId()))
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/events_with_me")
    public ResponseEntity<List<EventInfoResponse>> getEventsByParticipant(@AuthenticationPrincipal UserDetails details) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.getEventsByParticipant(userService.getCurrentUser(details).getId()))
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/my_events")
    public ResponseEntity<List<EventInfoResponse>> getEventsByUser(@AuthenticationPrincipal UserDetails details) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        User user = userService.getCurrentUser(details);
        for (var e : eventService.getEventsByOrganizer(user.getId()))
            dtos.add(mapper.toEventInfoResponse(e));
        for (var e : eventService.getEventsByParticipant(user.getId()))
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/event_types")
    public ResponseEntity<List<EventType>> getAllEventTypes() {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEventTypes());
    }
}
