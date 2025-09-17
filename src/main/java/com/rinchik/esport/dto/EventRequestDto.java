package com.rinchik.esport.dto;

import com.rinchik.esport.model.enums.EventType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequestDto {
    @NotBlank
    private String name;

    private String description;
    private LocalDateTime date;
    private EventType type;
    private Long organizerId;
}
