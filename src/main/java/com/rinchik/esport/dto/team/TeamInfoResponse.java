package com.rinchik.esport.dto.team;

import com.rinchik.esport.model.enums.Game;
import com.rinchik.esport.model.enums.TeamRole;
import lombok.Data;

import java.util.Map;

@Data
public class TeamInfoResponse {
    private Long id;
    private String name;
    private String description;
    private Game game;
    private Map<String, TeamRole> membersWithRoles;
}
