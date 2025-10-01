package com.rinchik.esport.service;

import com.rinchik.esport.dto.team.TeamChangesDto;
import com.rinchik.esport.dto.team.TeamCreatingRequest;
import com.rinchik.esport.dto.team.TeamInfoResponse;
import com.rinchik.esport.exception.*;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.Game;
import com.rinchik.esport.model.enums.TeamRole;
import com.rinchik.esport.repository.TeamRepository;
import com.rinchik.esport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {
    private final TeamRepository teamRepo;
    private final UserService userService;

    public List<Team> findAllTeams() {
        return teamRepo.findAll();
    }

    public Team findTeamByUser(User user) {
        if (user.getTeam() == null)
            throw new UserNotTeamMemberException(user.getId());

        return teamRepo.findById(user.getTeam().getId())
                .orElseThrow(() -> new TeamNotFoundException(user.getTeam().getId()));
    }

    public List<Team> findTeamsByGame(Game game) {
        return teamRepo.findByGame(game);
    }

    public TeamInfoResponse findTeamById(Long id) {
        Team team = teamRepo.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));
        TeamInfoResponse dto = new TeamInfoResponse();
        dto.setName(team.getName());
        dto.setDescription(team.getDescription());
        dto.setGame(team.getGame());
        List<User> members = team.getMembers();
        Map<String, TeamRole> membersWithRoles = new HashMap<>();
        for (var m : members)
            membersWithRoles.put(m.getLogin(), m.getRoleInTeam());
        dto.setMembersWithRoles(membersWithRoles);
        return dto;
    }

    @Transactional
    public Team createNewTeam(TeamCreatingRequest dto) {
        if (teamRepo.existsByName(dto.getName()))
            throw new TeamNameAlreadyTakenException(dto.getName());

        Team newTeam = new Team();
        newTeam.setName(dto.getName());
        newTeam.setDescription(dto.getDescription() != null ? dto.getDescription() : null);
        newTeam.setGame(dto.getGame());

        newTeam.setMembers(new ArrayList<>());

        return teamRepo.save(newTeam);
    }

    @Transactional
    public Team updateTeam(TeamChangesDto dto) {
        Team team = teamRepo.findById(dto.getId())
                .orElseThrow(() -> new TeamNotFoundException(dto.getId()));
        if (!teamRepo.existsByName(dto.getName()) ||
                teamRepo.existsByName(dto.getName()) &&
                        (teamRepo.findByName(dto.getName())
                                .orElseThrow(() -> new TeamNotFoundException(dto.getId())).getId() == dto.getId()))
            team.setName(dto.getName());
        else
            throw new TeamNameAlreadyTakenException(dto.getName());
        team.setDescription(dto.getDescription() != null ? dto.getDescription() : null);
        return team;
    }

    public List<User> findMembersByTeam(Long id) {
        Team team = teamRepo.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));
        return team.getMembers();
    }

//    @Transactional
//    public void addMemberToTeam(Long teamId, Long userId, TeamRole role) {
//        try {
//            Team team = teamRepo.findById(teamId)
//                    .orElseThrow(() -> new TeamNotFoundException(teamId));
//            User user = userService.findUserById(userId);
//
//            if (team.getMembers().contains(user))
//                throw new UserAlreadyTeamMemberException(userId, teamId);
//
//            team.getMembers().add(user);
//            user.setTeam(team);
//            user.setRoleInTeam(role);
//        } catch (UserNotFoundException e) {
//
//        }
//    }

//    @Transactional
//    public void deleteMemberFromTeam(Long teamId, Long userId) {
//        Team team = teamRepo.findById(teamId)
//                .orElseThrow(() -> new TeamNotFoundException(teamId));
//        User user = userRepo.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException(userId));
//
//        if (!team.getMembers().contains(user))
//            throw new UserNotTeamMemberException(userId, teamId);
//
//        team.getMembers().remove(user);
//        user.setTeam(null);
//        user.setRoleInTeam(null);
//    }

    @Transactional
    public void deleteTeam(Long id) {
        Team team = teamRepo.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));

        for (var m : team.getMembers()) {
            m.setTeam(null);
            m.setRoleInTeam(null);
        }
        team.getMembers().clear();

        teamRepo.delete(team);
    }
}
