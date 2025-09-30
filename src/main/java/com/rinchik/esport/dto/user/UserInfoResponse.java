package com.rinchik.esport.dto.user;

import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRole;
import lombok.Data;

import java.util.Set;

@Data
public class UserInfoResponse {
    private Long id;
    private String login;
    private Set<SystemRole> roles;
    private String email;
    private String description;
    private String teamName;
    private Long teamId;
    private TeamRole teamRole;
}
