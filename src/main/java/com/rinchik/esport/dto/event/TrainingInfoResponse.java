package com.rinchik.esport.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainingInfoResponse {
    @JsonProperty("event_id")
    private Long eventId;

    private LocalDateTime date;
    private String title;
}
