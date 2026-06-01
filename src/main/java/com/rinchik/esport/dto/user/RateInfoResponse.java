package com.rinchik.esport.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RateInfoResponse {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    private Double kd;
    //private Double adr;

    @JsonProperty("average_headshots")
    private Double averageHeadshots;

    @JsonProperty("win_rate")
    private Double winRate;

    @JsonProperty("tournament_played")
    private Double tournamentPlayed;

    @JsonProperty("training_attendance")
    private Double trainingAttendance;

    @JsonProperty("hours_played")
    private Double hoursPlayed;

    @JsonProperty("z_score")
    private Double zScore;

    @JsonProperty("rank_position")
    private int rankPosition;
}
