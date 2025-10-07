package com.rinchik.esport.controller;

import com.rinchik.esport.dto.team.TeamChangesRequest;
import com.rinchik.esport.dto.team.TeamCreatingRequest;
import com.rinchik.esport.dto.team.TeamInfoResponse;
import com.rinchik.esport.mapper.TeamMapper;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.Game;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRole;
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
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;
    private final UserService userService;
    private final TeamMapper mapper;

    @GetMapping("/my_team")
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public ResponseEntity<TeamInfoResponse> getTeamByUser(@AuthenticationPrincipal UserDetails details) {
        User user = userService.getCurrentUser(details);
        Team team = teamService.findTeamByUser(user);
        TeamInfoResponse dto = mapper.toTeamInfoResponse(team);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TeamInfoResponse>> getAllTeams() {
        List<Team> teams = teamService.findAllTeams();
        List<TeamInfoResponse> dtos = new ArrayList<>();
        for (var t : teams)
            dtos.add(mapper.toTeamInfoResponse(t));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @PostMapping("/new")
    @PreAuthorize("!hasAnyRole('ROLE_CAPTAIN', 'ROLE_PLAYER')")
    public ResponseEntity<TeamInfoResponse> createNewTeam(@AuthenticationPrincipal UserDetails details,
                                                          @Valid @RequestBody TeamCreatingRequest dto) {
        Team team = teamService.createNewTeam(dto);
        User user = userService.getCurrentUser(details);
        user = userService.addSystemRole(user.getId(), SystemRole.ROLE_CAPTAIN);
        user = userService.addSystemRole(user.getId(), SystemRole.ROLE_PLAYER);
        teamService.addMemberToTeam(team.getId(), user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toTeamInfoResponse(team));
    }

    @PutMapping("/my_team")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<TeamInfoResponse> updateTeam(@AuthenticationPrincipal UserDetails details,
                                                       @Valid @RequestBody TeamChangesRequest dto) {
        Team team = teamService.updateTeam(userService.getCurrentUser(details).getTeam().getId(), dto);
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toTeamInfoResponse(team));
    }

    @PostMapping("/members/{userId}")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> addMemberToTeam(@AuthenticationPrincipal UserDetails details,
                                                @PathVariable Long userId) {
        teamService.addMemberToTeam(userService.getCurrentUser(details).getTeam().getId(), userId);
        userService.addSystemRole(userId, SystemRole.ROLE_PLAYER);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/members/{userId}/role")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> changeMemberTeamRole(@AuthenticationPrincipal UserDetails details,
                                                     @PathVariable Long userId,
                                                     @RequestParam TeamRole role) {
        userService.changeTeamRoleByCaptain(userId, role, userService.getCurrentUser(details).getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/members/{userId}")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> deleteMemberFromTeam(@AuthenticationPrincipal UserDetails details,
                                                     @PathVariable Long userId) {
        teamService.deleteMemberFromTeamByCaptain(userService.getCurrentUser(details).getTeam().getId(),
                userId, userService.findUserByLogin(details.getUsername()).getId());
        userService.deleteSystemRole(userId, SystemRole.ROLE_PLAYER);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/my_team")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> deleteTeam(@AuthenticationPrincipal UserDetails details) {
        User currentUser = userService.getCurrentUser(details);
        for (var u : teamService.findMembersByTeam(currentUser.getTeam().getId()))
            userService.deleteSystemRole(u.getId(), SystemRole.ROLE_PLAYER);
        teamService.deleteTeam(currentUser.getTeam().getId());
        userService.deleteSystemRole(currentUser.getId(), SystemRole.ROLE_CAPTAIN);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamInfoResponse> getTeamById(@PathVariable Long teamId) {
        Team team = teamService.findTeamById(teamId);
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toTeamInfoResponse(team));
    }

    @GetMapping("/game")
    public ResponseEntity<List<TeamInfoResponse>> getTeamsByGame(@RequestParam Game game) {
        List<TeamInfoResponse> dtos = new ArrayList<>();
        for (var t : teamService.findTeamsByGame(game))
            dtos.add(mapper.toTeamInfoResponse(t));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/games")
    public ResponseEntity<List<Game>> getAllGames() {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getAllGames());
    }
}
