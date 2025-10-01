package com.rinchik.esport.dto.team;

import com.rinchik.esport.model.enums.Game;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TeamCreatingRequest {
    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private Game game;
}