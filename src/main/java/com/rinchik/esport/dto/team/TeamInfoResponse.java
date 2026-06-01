package com.rinchik.esport.dto.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rinchik.esport.dto.user.UserShortInfoResponse;
import com.rinchik.esport.model.enums.Game;
import com.rinchik.esport.model.enums.TeamRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class TeamInfoResponse {
    private Long id;
    private String name;
    private String description;

    @JsonProperty("created_date")
    private LocalDateTime createdDate;

    private String contacts;
    private String requirements;
    private Game game;
    private List<UserShortInfoResponse> members;

    @JsonProperty("team_roles")
    private Map<String, TeamRole> teamRoles;

    @JsonProperty("captain_id")
    private Long captainId;

    @JsonProperty("captain_name")
    private String captainName;
}
