package com.rinchik.esport.dto;

import com.rinchik.esport.model.enums.EventType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EventResponseDto {
    private Long id;
    private String name;
    private String description;
    private List<String> participantNames = new ArrayList<>();
    private List<Long> participantId = new ArrayList<>();
    private String organizerName;
    private Long organizerId;
    private String teamName;
    private Long teamId;
    private LocalDateTime date;
    private EventType type;
}
