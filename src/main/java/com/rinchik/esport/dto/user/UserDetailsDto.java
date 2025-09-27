package com.rinchik.esport.dto.user;

import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDetailsDto {
    @NotBlank
    private String login;

    @NotBlank
    private SystemRole role;

    @NotBlank
    private String email;

    private String description;
    private String teamName;
    private TeamRole teamRole;
}
