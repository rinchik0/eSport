package com.rinchik.esport.dto.user;

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
    private String avatarUrl;
    private LocalDateTime lastOnline;
    private LocalDateTime joinDate;
    private String teamName;
    private Long teamId;
    private TeamRole teamRole;
}
