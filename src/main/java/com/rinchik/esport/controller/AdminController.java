package com.rinchik.esport.controller;

import com.rinchik.esport.dto.event.EventCreatingRequest;
import com.rinchik.esport.dto.event.EventInfoResponse;
import com.rinchik.esport.dto.user.UserInfoResponse;
import com.rinchik.esport.exception.*;
import com.rinchik.esport.mapper.EventMapper;
import com.rinchik.esport.mapper.UserMapper;
import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.service.EventService;
import com.rinchik.esport.service.TeamService;
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
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/admin")
public class AdminController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TeamService teamService;

    @GetMapping("/events/all")
    public ResponseEntity<List<EventInfoResponse>> getAllEvents() {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.findAllEvents())
            dtos.add(eventMapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @PostMapping("/events/new")
    public ResponseEntity<EventInfoResponse> createNewCommonEvent(@AuthenticationPrincipal UserDetails details,
                                                                  @Valid @RequestBody EventCreatingRequest dto) {
        User user = userService.getCurrentUser(details);
        Event event = eventService.createNewEvent(user.getId(), null, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.toEventInfoResponse(event));
    }

    @PostMapping("/events/{teamId}/new")
    public ResponseEntity<EventInfoResponse> createNewTeamEvent(@AuthenticationPrincipal UserDetails details,
                                                                @Valid @RequestBody EventCreatingRequest dto,
                                                                @PathVariable Long teamId) {
        User user = userService.getCurrentUser(details);
        Event event = eventService.createNewEvent(user.getId(), teamId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.toEventInfoResponse(event));
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/events/{eventId}/participants")
    public ResponseEntity<Void> addParticipantToEvent(@RequestParam Long userId,
                                                      @PathVariable Long eventId) {
        eventService.addParticipantToEvent(eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/events/{eventId}/participants")
    public ResponseEntity<Void> deleteParticipantFromEvent(@RequestParam Long userId,
                                                           @PathVariable Long eventId) {
        eventService.deleteParticipantFromEvent(eventId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/teams/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        for (var u : teamService.findMembersByTeam(teamId)) {
            userService.deleteSystemRole(u.getId(), SystemRole.ROLE_PLAYER);
            userService.deleteSystemRole(u.getId(), SystemRole.ROLE_CAPTAIN);
        }
        teamService.deleteTeam(teamId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        List<UserInfoResponse> userDtos = new ArrayList<>();
        for (var user : users)
            userDtos.add(userMapper.toUserInfoResponse(user));
        return ResponseEntity.status(HttpStatus.OK).body(userDtos);
    }

    @PostMapping("/users/{userId}/role_admin")
    public ResponseEntity<Void> makeUserAdmin(@PathVariable Long userId) {
        userService.addSystemRole(userId, SystemRole.ROLE_ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/users/{userId}/role_admin")
    public ResponseEntity<Void> unmakeUserAdmin(@PathVariable Long userId) {
        userService.deleteSystemRole(userId, SystemRole.ROLE_ADMIN);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
