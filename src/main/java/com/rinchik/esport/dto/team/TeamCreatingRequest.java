package com.rinchik.esport.dto.team;

import com.rinchik.esport.model.enums.Game;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TeamCreatingRequest {
    @NotBlank(message = "Name of team can not be empty")
    @Size(min = 3, max = 50, message = "Name of team must be between 3 and 50 characters")
    private String name;

    private String description;

    @NotNull(message = "Game can not be empty")
    private Game game;
}