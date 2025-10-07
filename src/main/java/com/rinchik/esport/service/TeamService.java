package com.rinchik.esport.service;

import com.rinchik.esport.dto.team.TeamChangesRequest;
import com.rinchik.esport.dto.team.TeamCreatingRequest;
import com.rinchik.esport.exception.*;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.Game;
import com.rinchik.esport.model.enums.TeamRole;
import com.rinchik.esport.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {
    private final TeamRepository teamRepo;
    private final UserService userService;

    public List<Team> findAllTeams() {
        return teamRepo.findAll();
    }

    public Team findTeamById(Long id) {
        return teamRepo.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));
    }

    public Team findTeamByUser(User user) {
        if (user.getTeam() == null)
            throw new UserNotTeamMemberException(user.getId());

        return findTeamById(user.getTeam().getId());
    }

    public List<Team> findTeamsByGame(Game game) {
        return teamRepo.findByGame(game);
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
    public Team updateTeam(Long id, TeamChangesRequest dto) {
        Team team = findTeamById(id);
        if (!teamRepo.existsByName(dto.getName()) ||
                teamRepo.existsByName(dto.getName()) &&
                        (teamRepo.findByName(dto.getName())
                                .orElseThrow(() -> new TeamNotFoundException(id)).getId() == id))
            team.setName(dto.getName());
        else
            throw new TeamNameAlreadyTakenException(dto.getName());
        team.setDescription(dto.getDescription() != null ? dto.getDescription() : null);
        return team;
    }

    public List<User> findMembersByTeam(Long id) {
        Team team = findTeamById(id);
        return team.getMembers();
    }

    @Transactional
    public void addMemberToTeam(Long teamId, Long userId) {
        Team team = findTeamById(teamId);
        User user = userService.findUserById(userId);

        if (team.getMembers().contains(user))
            throw new UserAlreadyTeamMemberException(userId, teamId);

        team.getMembers().add(user);
        user.setTeam(team);
    }

    @Transactional
    public void deleteMemberFromTeam(Long teamId, Long userId) {
        Team team = findTeamById(teamId);
        User user = userService.findUserById(userId);

        if (!team.getMembers().contains(user))
            throw new UserNotTeamMemberException(userId, teamId);

        team.getMembers().remove(user);
        user.setTeam(null);
        user.setRoleInTeam(null);
    }

    @Transactional
    public void deleteMemberFromTeamByCaptain(Long teamId, Long userId, Long captainId) {
        if (userService.isFromOneTeam(userId, captainId))
            deleteMemberFromTeam(teamId, userId);
        else
            throw new NotCaptainOfTeamException(captainId, teamId);
    }

    @Transactional
    public void deleteTeam(Long id) {
        Team team = findTeamById(id);

        for (var m : team.getMembers()) {
            m.setTeam(null);
            m.setRoleInTeam(null);
        }
        team.getMembers().clear();

        teamRepo.delete(team);
    }

    public List<Game> getAllGames() {
        return Game.getAll();
    }
}
