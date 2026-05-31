package com.rinchik.esport.dto.team;

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
    private LocalDateTime createdDate;
    private String contacts;
    private String requirements;
    private Game game;
    private List<UserShortInfoResponse> members;
    private Map<String, TeamRole> teamRoles;
    private Long captainId;
    private String captainName;
}
