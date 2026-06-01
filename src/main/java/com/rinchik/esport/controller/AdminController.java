package com.rinchik.esport.controller;

import com.rinchik.esport.dto.methodology.MethodologyInfoResponse;
import com.rinchik.esport.dto.event.EventInfoResponse;
import com.rinchik.esport.dto.user.UserInfoResponse;
import com.rinchik.esport.mapper.MethodologyMapper;
import com.rinchik.esport.mapper.EventMapper;
import com.rinchik.esport.mapper.UserMapper;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.service.MethodologyService;
import com.rinchik.esport.service.EventService;
import com.rinchik.esport.service.TeamService;
import com.rinchik.esport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api")
public class AdminController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final TeamService teamService;
    private final MethodologyService methodologyService;
    private final MethodologyMapper methodologyMapper;

    @GetMapping("/events/all")
    public ResponseEntity<List<EventInfoResponse>> getAllEvents() {
        List<EventInfoResponse> dtos = new ArrayList<>();
        for (var e : eventService.findAllEvents())
            dtos.add(eventMapper.toEventInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @DeleteMapping("/teams/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        for (var u : teamService.findMembersByTeam(teamId)) {
            userService.deleteSystemRole(u.getId(), SystemRole.ROLE_PLAYER);
            userService.deleteSystemRole(u.getId(), SystemRole.ROLE_CAPTAIN);
            userService.addSystemRole(u.getId(), SystemRole.ROLE_GUEST);
        }
        teamService.deleteTeam(teamId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/methodologies/all")
    public ResponseEntity<List<MethodologyInfoResponse>> getAllMethodologies() {
        List<MethodologyInfoResponse> dtos = new ArrayList<>();
        for (var e : methodologyService.findAllMethodologies())
            dtos.add(methodologyMapper.toMethodologyInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
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
