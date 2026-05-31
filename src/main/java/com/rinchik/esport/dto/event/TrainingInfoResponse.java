package com.rinchik.esport.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainingInfoResponse {
    private Long eventId;
    private LocalDateTime date;
    private String title;
}
