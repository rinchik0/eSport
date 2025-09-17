package com.rinchik.esport.dto;

import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRole;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String name;
    private String login;
    private SystemRole systemRole;
    private TeamRole teamRole;
    private Long teamId;
    private String teamName;
}