package com.rinchik.esport.mapper;

import com.rinchik.esport.dto.team.ResponseTimeResponse;
import com.rinchik.esport.dto.team.TeamInfoResponse;
import com.rinchik.esport.dto.team.TeamRatesInfoResponse;
import com.rinchik.esport.dto.teamrequest.TeamRequestInfoResponse;
import com.rinchik.esport.dto.user.UserShortInfoResponse;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.TeamRequest;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.TeamRole;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TeamMapper {
    public TeamInfoResponse toTeamInfoResponse(Team team) {
        TeamInfoResponse dto = new TeamInfoResponse();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setDescription(team.getDescription());
        dto.setGame(team.getGame());
        dto.setCreatedDate(team.getCreatedDate());
        dto.setContacts(team.getContacts());
        dto.setRequirements(team.getRequirements());

        List<UserShortInfoResponse> members = new ArrayList<>();
        Map<String, TeamRole> teamRoles = new HashMap<>();
        for (var m : team.getMembers()) {
            members.add(toTeamMemberInfoResponse(m));
            teamRoles.put(m.getUsername(), m.getRoleInTeam());
        }
        dto.setMembers(members);
        dto.setTeamRoles(teamRoles);

        dto.setCaptainId(team.getCaptain().getId());
        dto.setCaptainName(team.getCaptain().getUsername());
        return dto;
    }

    public TeamRequestInfoResponse toTeamRequestInfoResponse(TeamRequest teamRequest) {
        TeamRequestInfoResponse dto = new TeamRequestInfoResponse();
        dto.setId(teamRequest.getId());
        dto.setUserId(teamRequest.getUser().getId());
        dto.setUserName(teamRequest.getUser().getUsername());
        dto.setTeamId(teamRequest.getTeam().getId());
        dto.setTeamName(teamRequest.getTeam().getName());
        dto.setMessage(teamRequest.getMessage());
        dto.setCreatedDate(teamRequest.getCreatedDate());
        dto.setStatus(teamRequest.getStatus());
        return dto;
    }

    public UserShortInfoResponse toTeamMemberInfoResponse(User user) {
        UserShortInfoResponse dto = new UserShortInfoResponse();
        dto.setUserId(user.getId());
        dto.setUserName(dto.getUserName());
        return dto;
    }

    public TeamRatesInfoResponse toTeamRatesInfoResponse(Team team, Double zScore, int rankPosition) {
        TeamRatesInfoResponse dto = new TeamRatesInfoResponse();
        dto.setTeamId(team.getId());
        dto.setTeamName(team.getName());
        dto.setRankPosition(rankPosition);
        dto.setZScore(zScore);
        return dto;
    }

    public ResponseTimeResponse toResponseTeamResponse(long time) {
        ResponseTimeResponse dto = new ResponseTimeResponse();
        dto.setTime(time);
        return dto;
    }
}
