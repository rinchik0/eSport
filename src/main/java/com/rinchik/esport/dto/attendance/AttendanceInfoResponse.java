package com.rinchik.esport.dto.attendance;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceInfoResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long eventId;
    private LocalDateTime trainingDate;
    private boolean attended;
}
