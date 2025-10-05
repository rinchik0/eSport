package com.rinchik.esport.controller;

import com.rinchik.esport.dto.event.EventCreatingRequest;
import com.rinchik.esport.dto.event.EventInfoResponse;
import com.rinchik.esport.exception.*;
import com.rinchik.esport.mapper.EventMapper;
import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.Game;
import com.rinchik.esport.service.EventService;
import com.rinchik.esport.service.UserService;
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

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<EventInfoResponse>> getAll() {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.findAllEvents())
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public ResponseEntity<List<EventInfoResponse>> getAll(@AuthenticationPrincipal UserDetails details) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.findEventsByTeam(userService.findUserByLogin(details.getUsername()).getTeam().getId()))
            dtos.add(mapper.toEventInfoResponse(e));
        for (var e : eventService.findCommonEvents())
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/team_events")
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public ResponseEntity<List<EventInfoResponse>> getEventsByTeam(@AuthenticationPrincipal UserDetails details) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.findEventsByTeam(userService.findUserByLogin(details.getUsername()).getTeam().getId()))
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/game_{game}")
    public ResponseEntity<List<EventInfoResponse>> getEventsByGame(@PathVariable Game game) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.findEventsByGame(game))
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/id_{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            Event event = eventService.findEventById(id);
            return ResponseEntity.status(HttpStatus.OK).body(mapper.toEventInfoResponse(event));
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/create_new_teamevent")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<?> createNewTeamEvent(@AuthenticationPrincipal UserDetails details,
                                                @RequestBody EventCreatingRequest dto) {
        try {
            User user = userService.findUserByLogin(details.getUsername());
            Event event = eventService.createNewEvent(user.getId(), user.getTeam().getId(), dto);
            return ResponseEntity.status(HttpStatus.OK).body(mapper.toEventInfoResponse(event));
        } catch (UserNotFoundException | TeamNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/create_new_event")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createNewCommonEvent(@AuthenticationPrincipal UserDetails details,
                                                  @RequestBody EventCreatingRequest dto) {
        try {
            User user = userService.findUserByLogin(details.getUsername());
            Event event = eventService.createNewEvent(user.getId(), null, dto);
            return ResponseEntity.status(HttpStatus.OK).body(mapper.toEventInfoResponse(event));
        } catch (UserNotFoundException | TeamNotFoundException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @PostMapping("/create_new_event")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createNewEvent(@AuthenticationPrincipal UserDetails details,
                                                 @RequestBody EventCreatingRequest dto,
                                                 @RequestParam Long teamId) {
        try {
            User user = userService.findUserByLogin(details.getUsername());
            Event event = eventService.createNewEvent(user.getId(), teamId, dto);
            return ResponseEntity.status(HttpStatus.OK).body(mapper.toEventInfoResponse(event));
        } catch (UserNotFoundException | TeamNotFoundException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @PostMapping("/delete_teamevent")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<String> deleteTeamEvent(@AuthenticationPrincipal UserDetails details,
                                                  @RequestParam Long eventId) {
        try {
            User user = userService.findUserByLogin(details.getUsername());
            if (eventService.isUserOrganizerForEvent(user.getId(), eventId)) {
                eventService.deleteEvent(eventId);
                return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted");
            } else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed");
        } catch (UserNotFoundException | EventNotFoundException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @PostMapping("/delete_event")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteEvent(@RequestParam Long eventId) {
        try {
            eventService.deleteEvent(eventId);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted");
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/participants_of_event_{id}")
    public ResponseEntity<?> getParticipantsOfEvent(@PathVariable Long eventId) {
        try {
            List<String> dtos = new ArrayList<>();
            for (var p : eventService.findParticipantsByEvent(eventId))
                dtos.add(p.getLogin());
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/add_participant")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<String> addParticipantToEvent(@AuthenticationPrincipal UserDetails details,
                                                        @RequestParam Long userId,
                                                        @RequestParam Long eventId) {
        try {
            User user = userService.findUserByLogin(details.getUsername());
            Event event = eventService.findEventById(eventId);
            if (user.getId().equals(event.getOrganizer().getId())) {
                eventService.addParticipantToEvent(eventId, userId);
                return ResponseEntity.status(HttpStatus.OK).body("Successfully added");
            } else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed");
        } catch (UserAlreadyEventParticipantException | EventNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/add_participant")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> addParticipantToEvent(@RequestParam Long userId,
                                                        @RequestParam Long eventId) {
        try {
            eventService.addParticipantToEvent(eventId, userId);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully added");
        } catch (UserAlreadyEventParticipantException | EventNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/delete_participant")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<String> deleteParticipantFromEvent(@AuthenticationPrincipal UserDetails details,
                                                             @RequestParam Long userId,
                                                             @RequestParam Long eventId) {
        try {
            User user = userService.findUserByLogin(details.getUsername());
            Event event = eventService.findEventById(eventId);
            if (user.getId().equals(event.getOrganizer().getId())) {
                eventService.deleteParticipantFromEvent(eventId, userId);
                return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted");
            } else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed");
        } catch (UserNotEventParticipantException | EventNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/delete_participant")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteParticipantFromEvent(@RequestParam Long userId,
                                                             @RequestParam Long eventId) {
        try {
            eventService.deleteParticipantFromEvent(eventId, userId);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted");
        } catch (UserNotEventParticipantException | EventNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/events_organized_by_me")
    public ResponseEntity<List<EventInfoResponse>> getEventsByOrganizer(@AuthenticationPrincipal UserDetails details) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.getEventsByOrganizer(userService.findUserByLogin(details.getUsername()).getId()))
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/events_with_me")
    public ResponseEntity<List<EventInfoResponse>> getEventsByParticipant(@AuthenticationPrincipal UserDetails details) {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.getEventsByParticipant(userService.findUserByLogin(details.getUsername()).getId()))
            dtos.add(mapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }
}
