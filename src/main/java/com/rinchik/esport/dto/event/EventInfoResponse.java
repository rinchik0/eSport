package com.rinchik.esport.dto.event;

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
    private Long organizerId;
    private String organizerName;
    private Long teamId;
    private String teamName;
    private int maxAmountOfParticipants;
    private int amountOfParticipants;
    private EventStatus status;
    private String prize;
}
