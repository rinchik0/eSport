package com.rinchik.esport.controller;

import com.rinchik.esport.dto.team.*;
import com.rinchik.esport.dto.teamrequest.TeamRequestInfoResponse;
import com.rinchik.esport.dto.teamrequest.TeamRequestMessageRequest;
import com.rinchik.esport.dto.user.UserShortInfoResponse;
import com.rinchik.esport.mapper.TeamMapper;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.TeamRequest;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.Game;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRequestStatus;
import com.rinchik.esport.model.enums.TeamRole;
import com.rinchik.esport.service.TeamRequestService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;
    private final UserService userService;
    private final TeamRequestService teamRequestService;
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
    public ResponseEntity<List<TeamInfoResponse>> getAllTeams(@RequestParam(required = false) Game game) {
        List<Team> teams;
        if (game != null)
            teams = teamService.findTeamsByGame(game);
        else
            teams = teamService.findAllTeams();
        List<TeamInfoResponse> dtos = new ArrayList<>();
        for (var t : teams)
            dtos.add(mapper.toTeamInfoResponse(t));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public ResponseEntity<TeamInfoResponse> createNewTeam(@AuthenticationPrincipal UserDetails details,
                                                          @Valid @RequestBody TeamCreatingRequest dto) {
        User user = userService.getCurrentUser(details);
        Team team = teamService.createNewTeam(dto, user.getId());
        user = userService.addSystemRole(user.getId(), SystemRole.ROLE_CAPTAIN);
        user = userService.addSystemRole(user.getId(), SystemRole.ROLE_PLAYER);
        user = userService.deleteSystemRole(user.getId(), SystemRole.ROLE_GUEST);
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

    @GetMapping("/my_team/members")
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public ResponseEntity<List<UserShortInfoResponse>> getMembersByPlayer(@AuthenticationPrincipal UserDetails details) {
        Team team = teamService.findTeamByUser(userService.getCurrentUser(details));
        List<UserShortInfoResponse> dtos = new ArrayList<>();
        for (User u : team.getMembers())
            dtos.add(mapper.toTeamMemberInfoResponse(u));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @PutMapping("/my_team/members/{userId}/role")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> changeMemberTeamRole(@AuthenticationPrincipal UserDetails details,
                                                     @PathVariable Long userId,
                                                     @Valid @RequestBody TeamRoleChangesRequest dto) {
        userService.changeTeamRoleByCaptain(userId, dto.getRole(), userService.getCurrentUser(details).getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/my_team/members/{userId}")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> deleteMemberFromTeam(@AuthenticationPrincipal UserDetails details,
                                                     @PathVariable Long userId) {
        teamService.deleteMemberFromTeamByCaptain(userService.getCurrentUser(details).getTeam().getId(),
                userId, userService.getCurrentUser(details).getId());
        userService.deleteSystemRole(userId, SystemRole.ROLE_PLAYER);
        userService.addSystemRole(userId, SystemRole.ROLE_GUEST);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/my_team")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> deleteTeam(@AuthenticationPrincipal UserDetails details) {
        User currentUser = userService.getCurrentUser(details);
        for (var u : teamService.findMembersByTeam(currentUser.getTeam().getId())) {
            userService.deleteSystemRole(u.getId(), SystemRole.ROLE_PLAYER);
            userService.addSystemRole(u.getId(), SystemRole.ROLE_GUEST);
        }
        teamService.deleteTeam(currentUser.getTeam().getId());
        userService.deleteSystemRole(currentUser.getId(), SystemRole.ROLE_CAPTAIN);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamInfoResponse> getTeamById(@PathVariable Long teamId) {
        Team team = teamService.findTeamById(teamId);
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toTeamInfoResponse(team));
    }

    @GetMapping("/{teamId}/response_time")
    public ResponseEntity<ResponseTimeResponse> getResponseTimeByTeam(@PathVariable Long teamId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                mapper.toResponseTeamResponse(teamRequestService.getResponseTimeByTeam(teamId)));
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<UserShortInfoResponse>> getMembersByTeam(@PathVariable Long teamId) {
        Team team = teamService.findTeamById(teamId);
        List<UserShortInfoResponse> dtos = new ArrayList<>();
        for (User u : team.getMembers())
            dtos.add(mapper.toTeamMemberInfoResponse(u));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/games")
    public ResponseEntity<List<Game>> getAllGames() {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getAllGames());
    }

    @DeleteMapping("/leave_team")
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public ResponseEntity<Void> userLeavesTeam(@AuthenticationPrincipal UserDetails details) {
        User user = userService.getCurrentUser(details);
        if (user.getRoles().contains(SystemRole.ROLE_CAPTAIN))
            return deleteTeam(details);
        teamService.deleteMemberFromTeam(user.getTeam().getId(), user.getId());
        userService.deleteSystemRole(user.getId(), SystemRole.ROLE_PLAYER);
        userService.addSystemRole(user.getId(), SystemRole.ROLE_GUEST);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/change_captain/{userId}")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> changeTeamCaptain(@AuthenticationPrincipal UserDetails details,
                                                  @PathVariable Long userId) {
        User oldCaptain = userService.getCurrentUser(details);
        teamService.changeCaptain(oldCaptain.getId(), userId);
        userService.deleteSystemRole(oldCaptain.getId(), SystemRole.ROLE_CAPTAIN);
        userService.addSystemRole(userId, SystemRole.ROLE_CAPTAIN);
        userService.addSystemRole(userId, SystemRole.ROLE_PLAYER);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{teamId}/requests/new")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public ResponseEntity<Void> sendRequestToTeam(@AuthenticationPrincipal UserDetails details,
                                                  @Valid @RequestBody TeamRequestMessageRequest dto,
                                                  @PathVariable Long teamId) {
        teamRequestService.createNewTeamRequest(dto, userService.getCurrentUser(details), teamId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/my_team/requests")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<List<TeamRequestInfoResponse>> getTeamRequestByCaptain(@AuthenticationPrincipal UserDetails details,
                                                                                 @RequestParam(required = false) Integer wKd,
                                                                                 //@RequestParam(required = false) Integer wAdr,
                                                                                 @RequestParam(required = false) Integer wHs,
                                                                                 @RequestParam(required = false) Integer wWr,
                                                                                 @RequestParam(required = false) Integer wTa,
                                                                                 @RequestParam(required = false) Integer wTp,
                                                                                 @RequestParam(required = false) Integer wHp) {
        ArrayList<Integer> weights = new ArrayList<>();
        weights.add(wKd);
        weights.add(wHs);
        weights.add(wWr);
        weights.add(wTa);
        weights.add(wTp);
        weights.add(wHp);
        User user = userService.getCurrentUser(details);
        List<TeamRequest> requests = teamRequestService.findTeamRequestsRanking(user.getTeam(), weights);
        List<TeamRequestInfoResponse> dtos = new ArrayList<>();
        for (TeamRequest r : requests)
            dtos.add(mapper.toTeamRequestInfoResponse(r));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @PostMapping("/my_team/requests/{teamRequestId}")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> acceptTeamRequestByCaptain(@AuthenticationPrincipal UserDetails details,
                                                           @PathVariable Long teamRequestId) {
        User captain = userService.getCurrentUser(details);
        TeamRequest request = teamRequestService.acceptTeamRequestByCaptain(teamRequestId, captain);
        User newPlayer = request.getUser();
        teamService.addMemberToTeam(captain.getCaptainedTeam().getId(), newPlayer.getId());
        userService.addSystemRole(newPlayer.getId(), SystemRole.ROLE_PLAYER);
        userService.deleteSystemRole(newPlayer.getId(), SystemRole.ROLE_GUEST);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("my_team/requests/{teamRequestId}")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> declineTeamRequestByCaptain(@AuthenticationPrincipal UserDetails details,
                                                            @PathVariable Long teamRequestId) {
        User captain = userService.getCurrentUser(details);
        teamRequestService.declineTeamRequestByCaptain(teamRequestId, captain);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/requests/my_requests")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public ResponseEntity<List<TeamRequestInfoResponse>> getTeamRequestByUser(@AuthenticationPrincipal UserDetails details) {
        List<TeamRequestInfoResponse> dtos = new ArrayList<>();
        List <TeamRequest> requests = teamRequestService.findTeamRequestsByUser(userService.getCurrentUser(details));
        for (TeamRequest r : requests)
            dtos.add(mapper.toTeamRequestInfoResponse(r));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @DeleteMapping("/requests/{teamRequestId}")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public ResponseEntity<Void> deleteTeamRequest(@AuthenticationPrincipal UserDetails details,
                                                  @PathVariable Long teamRequestId) {
        teamRequestService.deleteTeamRequestByUser(teamRequestId, userService.getCurrentUser(details));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
