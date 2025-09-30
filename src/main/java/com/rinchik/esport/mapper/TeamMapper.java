package com.rinchik.esport.mapper;

import com.rinchik.esport.dto.team.TeamInfoResponse;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.enums.TeamRole;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TeamMapper {
    public TeamInfoResponse toTeamInfoResponse(Team team) {
        TeamInfoResponse dto = new TeamInfoResponse();

        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setDescription(team.getDescription());
        dto.setGame(team.getGame());

        if (!team.getMembers().isEmpty()) {
            Map<String, TeamRole> members = new HashMap<>();
            for (var m : team.getMembers())
                members.put(m.getLogin(), m.getRoleInTeam());

            dto.setMembersWithRoles(members);
        }
        else dto.setMembersWithRoles(null);

        return dto;
    }
}
