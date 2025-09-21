package com.rinchik.esport.dto.team;

import com.rinchik.esport.model.enums.Game;
import lombok.Data;

@Data
public class TeamDetailsDto {
    private Long id;
    private String name;
    private String description;
    private Game game;
}
