package com.rinchik.esport.dto.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TeamRatesInfoResponse {
    @JsonProperty("team_id")
    private Long teamId;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("rank_position")
    private int rankPosition;

    @JsonProperty("z_score")
    private Double zScore;
}
