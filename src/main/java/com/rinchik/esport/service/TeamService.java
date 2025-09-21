package com.rinchik.esport.service;

import com.rinchik.esport.dto.team.TeamChangesDto;
import com.rinchik.esport.dto.team.TeamCreatingDto;
import com.rinchik.esport.dto.team.TeamDetailsDto;
import com.rinchik.esport.dto.team.TeamInfoWithMembersDto;
import com.rinchik.esport.dto.user.UserDetailsDto;
import com.rinchik.esport.exception.TeamNameAlreadyTakenException;
import com.rinchik.esport.exception.TeamNotFoundException;
import com.rinchik.esport.exception.UserNotFoundException;
import com.rinchik.esport.exception.UserNotTeamMemberException;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {
    private final TeamRepository teamRepo;
    private final UserRepository userRepo;

    public List<Team> findAllTeams() {
        return teamRepo.findAll();
    }

    public List<TeamDetailsDto> findAllTeamsDto() {
        List<Team> teams = teamRepo.findAll();
        List<TeamDetailsDto> dtos = new ArrayList<>();
        for (var t : teams) {
            TeamDetailsDto dto = new TeamDetailsDto();
            dto.setId(t.getId());
            dto.setName(t.getName());
            dto.setDescription(t.getDescription());
            dto.setGame(t.getGame());
            dtos.addLast(dto);
        }
        return dtos;
    }

    public List<Team> findTeamsByGame(Game game) {
        return teamRepo.findByGame(game);
    }

    public TeamInfoWithMembersDto findTeamById(Long id) {
        Team team = teamRepo.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));
        TeamInfoWithMembersDto dto = new TeamInfoWithMembersDto();
        dto.setName(team.getName());
        dto.setDescription(team.getDescription());
        dto.setGame(team.getGame());
        List<User> members = team.getMembers();
        List<String> membersNames = new ArrayList<>();
        for (var m : members)
            membersNames.addLast(m.getName());
        dto.setMembersNames(membersNames);
        return dto;
    }

    public Team createNewTeam(TeamCreatingDto dto) {
        if (teamRepo.existByName(dto.getName()))
            throw new TeamNameAlreadyTakenException(dto.getName());

        Team newTeam = new Team();
        newTeam.setName(dto.getName());
        newTeam.setDescription(dto.getDescription() != null ? dto.getDescription() : null);
        newTeam.setGame(dto.getGame());

        newTeam.setMembers(new ArrayList<>());

        return teamRepo.save(newTeam);
    }

    public Team updateTeam(TeamChangesDto dto) {
        Team team = teamRepo.findById(dto.getId())
                .orElseThrow(() -> new TeamNotFoundException(dto.getId()));
        if (!teamRepo.existByNameAndNotId(dto.getName(), dto.getId()))
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

    public void addMemberToTeam(Long teamId, Long userId, TeamRole role) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        team.getMembers().add(user);
        user.setTeam(team);
        user.setRoleInTeam(role);
    }

    public void deleteMemberFromTeam(Long teamId, Long userId) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!team.getMembers().contains(user))
            throw new UserNotTeamMemberException(userId, teamId);

        team.getMembers().remove(user);
        user.setTeam(null);
        user.setRoleInTeam(null);
    }

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
