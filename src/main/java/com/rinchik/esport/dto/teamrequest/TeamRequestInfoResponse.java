package com.rinchik.esport.dto.teamrequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rinchik.esport.model.enums.TeamRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamRequestInfoResponse {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("team_id")
    private Long teamId;

    @JsonProperty("team_name")
    private String teamName;

    private String message;

    @JsonProperty("created_date")
    private LocalDateTime createdDate;

    private TeamRequestStatus status;
}
