package com.rinchik.esport.dto.team;

import com.rinchik.esport.model.enums.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamRoleChangesRequest {
    @NotNull
    private TeamRole role;
}
