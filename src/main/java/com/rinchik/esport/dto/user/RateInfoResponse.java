package com.rinchik.esport.dto.user;

import lombok.Data;

@Data
public class RateInfoResponse {
    private Long userId;
    private String userName;
    private Double kd;
    private Double adr;
    private Double winRate;
    private Double tournamentPlayed;
    private Double trainingAttendance;
    private Double hoursPlayed;
    private Double zScore;
    private int rankPosition;
}
