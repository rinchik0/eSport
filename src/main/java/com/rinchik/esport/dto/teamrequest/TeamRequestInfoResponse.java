package com.rinchik.esport.dto.teamrequest;

import com.rinchik.esport.model.enums.TeamRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamRequestInfoResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long teamId;
    private String teamName;
    private String message;
    private LocalDateTime createdDate;
    private TeamRequestStatus status;
}
