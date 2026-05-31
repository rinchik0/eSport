package com.rinchik.esport.dto.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttendanceCreatingRequest {
    @NotNull
    private boolean attended;
}
