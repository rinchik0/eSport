package com.rinchik.esport.controller;

import com.rinchik.esport.dto.team.TeamChangesRequest;
import com.rinchik.esport.dto.team.TeamCreatingRequest;
import com.rinchik.esport.dto.team.TeamInfoResponse;
import com.rinchik.esport.exception.*;
import com.rinchik.esport.mapper.TeamMapper;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.Game;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRole;
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

    @PostMapping("/create_new")
    @PreAuthorize("!hasAnyRole('ROLE_CAPTAIN', 'ROLE_PLAYER')")
    public ResponseEntity<?> createNewTeam(@AuthenticationPrincipal UserDetails details,
                                           @RequestBody TeamCreatingRequest dto) {
        try {
            Team team = teamService.createNewTeam(dto);
            User user = userService.findUserByLogin(details.getUsername());
            user = userService.addSystemRole(user.getId(), SystemRole.ROLE_CAPTAIN);
            user = userService.addSystemRole(user.getId(), SystemRole.ROLE_PLAYER);
            teamService.addMemberToTeam(team.getId(), user.getId());
            return ResponseEntity.status(HttpStatus.OK).body(mapper.toTeamInfoResponse(team));
        } catch (UserNotFoundException | TeamNameAlreadyTakenException | TeamNotFoundException |
                 UserAlreadyTeamMemberException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/change_team")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<?> updateTeam(@AuthenticationPrincipal UserDetails details,
                                        @RequestBody TeamChangesRequest dto) {
        try {
            Team team = teamService.updateTeam(userService.findUserByLogin(details.getUsername()).getTeam().getId(), dto);
            return ResponseEntity.status(HttpStatus.OK).body(mapper.toTeamInfoResponse(team));
        } catch (TeamNameAlreadyTakenException | TeamNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/add_member")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<String> addMemberToTeam(@AuthenticationPrincipal UserDetails details,
                                                  @RequestParam Long userId) {
        try {
            teamService.addMemberToTeam(userService.findUserByLogin(details.getUsername()).getTeam().getId(), userId);
            userService.addSystemRole(userId, SystemRole.ROLE_PLAYER);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully added");
        } catch (UserAlreadyTeamMemberException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/change_teamrole")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<String> changeMemberTeamRole(@AuthenticationPrincipal UserDetails details,
                                                       @RequestParam Long userId, @RequestParam TeamRole role) {
        try {
            if (userService.findUserById(userId).getTeam().equals(userService.findUserByLogin(details.getUsername()).getTeam())) {
                userService.changeTeamRole(userId, role);
                return ResponseEntity.status(HttpStatus.OK).body("Successfully changed");
            } else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/delete_member")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<String> deleteMemberFromTeam(@AuthenticationPrincipal UserDetails details,
                                                       @RequestParam Long userId) {
        try {
            if (userService.findUserById(userId).getTeam().equals(userService.findUserByLogin(details.getUsername()).getTeam())) {
                teamService.deleteMemberFromTeam(userService.findUserByLogin(details.getUsername()).getTeam().getId(), userId);
                userService.deleteSystemRole(userId, SystemRole.ROLE_PLAYER);
                return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted");
            } else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed");
        } catch (TeamNotFoundException | UserNotTeamMemberException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/delete_team")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<String> deleteTeam(@AuthenticationPrincipal UserDetails details) {
        try {
            for (var u : teamService.findMembersByTeam(userService.findUserByLogin(details.getUsername()).getTeam().getId()))
                userService.deleteSystemRole(u.getId(), SystemRole.ROLE_PLAYER);
            teamService.deleteTeam(userService.findUserByLogin(details.getUsername()).getTeam().getId());
            userService.deleteSystemRole(userService.findUserByLogin(details.getUsername()).getId(), SystemRole.ROLE_CAPTAIN);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted");
        } catch (TeamNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/delete_team")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteTeam(@RequestParam Long teamId) {
        try {
            for (var u : teamService.findMembersByTeam(teamId)) {
                userService.deleteSystemRole(u.getId(), SystemRole.ROLE_PLAYER);
                userService.deleteSystemRole(u.getId(), SystemRole.ROLE_CAPTAIN);
            }
            teamService.deleteTeam(teamId);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted");
        } catch (TeamNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/id_{id}")
    public ResponseEntity<?> getTeamById(@PathVariable Long id) {
        try {
            Team team = teamService.findTeamById(id);
            return ResponseEntity.status(HttpStatus.OK).body(mapper.toTeamInfoResponse(team));
        } catch (TeamNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/game_{game}")
    public ResponseEntity<List<TeamInfoResponse>> getTeamsByGame(@PathVariable Game game) {
        List<TeamInfoResponse> dtos = new ArrayList<>();
        for (var t : teamService.findTeamsByGame(game))
            dtos.add(mapper.toTeamInfoResponse(t));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }
}
