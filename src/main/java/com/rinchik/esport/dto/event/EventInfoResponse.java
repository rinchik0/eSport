package com.rinchik.esport.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rinchik.esport.model.enums.EventType;
import com.rinchik.esport.model.enums.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventInfoResponse {
    private Long id;
    private String title;
    private String description;
    private EventType type;
    private LocalDateTime date;

    @JsonProperty("organizer_id")
    private Long organizerId;

    @JsonProperty("organizer_name")
    private String organizerName;

    @JsonProperty("team_id")
    private Long teamId;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("max_amount_of_participants")
    private int maxAmountOfParticipants;

    @JsonProperty("amount_of_participants")
    private int amountOfParticipants;

    private EventStatus status;
    private String prize;
}
