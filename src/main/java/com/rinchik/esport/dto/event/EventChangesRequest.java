package com.rinchik.esport.dto.event;

import com.rinchik.esport.model.enums.EventType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventChangesRequest {
    @Size(min = 3, max = 50, message = "Title of event must be between 3 and 50 characters")
    private String title;

    private String description;
    private Integer maxAmountOfParticipants;
    private String prize;

    @Future(message = "Date of event must be in the future")
    private LocalDateTime date;
}
