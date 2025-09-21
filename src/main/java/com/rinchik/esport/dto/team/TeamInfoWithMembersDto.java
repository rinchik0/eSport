package com.rinchik.esport.dto.team;

import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.Game;
import lombok.Data;

import java.util.List;

@Data
public class TeamInfoWithMembersDto {
    private String name;
    private String description;
    private Game game;
    private List<String> membersNames;
}
