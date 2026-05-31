package com.rinchik.esport.mapper;

import com.rinchik.esport.dto.attendance.AttendanceInfoResponse;
import com.rinchik.esport.model.TrainingAttendance;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AttendanceMapper {
    public AttendanceInfoResponse toAttendanceInfoResponse(TrainingAttendance att) {
        AttendanceInfoResponse dto = new AttendanceInfoResponse();
        dto.setId(att.getId());
        dto.setUserName(att.getUser().getUsername());
        dto.setUserId(att.getUser().getId());
        dto.setEventId(att.getTraining().getId());
        dto.setTrainingDate(att.getTraining().getDate());
        dto.setAttended(att.isAttended());
        return dto;
    }
}
