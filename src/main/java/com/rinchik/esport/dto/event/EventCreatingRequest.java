package com.rinchik.esport.dto.event;

import com.rinchik.esport.model.enums.EventType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventCreatingRequest {
    @NotBlank(message = "Name of event can not be empty")
    @Size(min = 3, max = 50, message = "Name of event must be between 3 and 50 characters")
    private String name;

    private String description;

    @NotNull(message = "Type of event can not be empty")
    private EventType type;

    @NotNull(message = "Date of event is required")
    @Future(message = "Date of event must be in the future")
    private LocalDateTime date;
}
