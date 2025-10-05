package com.rinchik.esport.dto.event;

import com.rinchik.esport.model.enums.EventType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventCreatingRequest {
    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private EventType type;

    @NotBlank
    private LocalDateTime date;
}
