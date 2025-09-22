package com.rinchik.esport.dto.event;

import com.rinchik.esport.model.enums.EventType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventCreatingDto {
    private String name;
    private String description;
    private EventType type;
    private LocalDateTime date;
    private Long organizerId;
    private Long teamId;
}
