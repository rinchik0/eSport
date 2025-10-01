package com.rinchik.esport.controller;

import com.rinchik.esport.dto.team.TeamCreatingRequest;
import com.rinchik.esport.dto.team.TeamInfoResponse;
import com.rinchik.esport.exception.TeamNotFoundException;
import com.rinchik.esport.exception.UserNotFoundException;
import com.rinchik.esport.exception.UserNotTeamMemberException;
import com.rinchik.esport.mapper.TeamMapper;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.User;
import com.rinchik.esport.service.TeamService;
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
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;
    private final UserService userService;
    private final TeamMapper mapper;

    @GetMapping("/my_team")
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public ResponseEntity<?> getTeamByUser(@AuthenticationPrincipal UserDetails details) {
        try {
            User user = userService.findUserByLogin(details.getUsername());
            Team team = teamService.findTeamByUser(user);
            TeamInfoResponse dto = mapper.toTeamInfoResponse(team);
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } catch (UserNotFoundException | UserNotTeamMemberException | TeamNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<TeamInfoResponse>> getAllTeams() {
        List<Team> teams = teamService.findAllTeams();
        List<TeamInfoResponse> dtos = new ArrayList<>();
        for (var t : teams)
            dtos.add(mapper.toTeamInfoResponse(t));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

//    @PostMapping("/create_new")
//    public ResponseEntity<?> createNewTeam(@AuthenticationPrincipal UserDetails details,
//                                           @RequestBody TeamCreatingRequest dto) {
//        try {
//            Team team = teamService.createNewTeam(dto);
//            User user = userService.findUserByLogin(details.getUsername());
//
//        } catch (UserNotFoundException | ) {}
//    }
}
