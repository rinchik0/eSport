package com.rinchik.esport.dto.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceInfoResponse {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("event_id")
    private Long eventId;

    @JsonProperty("training_date")
    private LocalDateTime trainingDate;

    private boolean attended;
}
