package com.rinchik.esport.dto.event;

import com.rinchik.esport.model.enums.EventType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventInfoWithParticipantsDto {
    private String name;
    private String description;
    private EventType type;
    private LocalDateTime date;
    private String organizerName;
    private List<String> participantNames;
    private String teamName;
}
