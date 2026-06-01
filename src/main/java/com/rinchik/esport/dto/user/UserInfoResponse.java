package com.rinchik.esport.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private Set<SystemRole> roles;
    private String email;
    private String bio;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("last_online")
    private LocalDateTime lastOnline;

    @JsonProperty("join_date")
    private LocalDateTime joinDate;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("team_id")
    private Long teamId;

    @JsonProperty("team_role")
    private TeamRole teamRole;

    @JsonProperty("faceit_nickname")
    private String faceitNickname;

    @JsonProperty("faceit_player_id")
    private String faceitPlayerId;

    @JsonProperty("steam_id")
    private String steamId;
}
