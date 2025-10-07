package com.rinchik.esport.dto.team;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TeamChangesRequest {
    @Size(min = 3, max = 50, message = "Name of team must be between 3 and 50 characters")
    private String name;

    private String description;
}
