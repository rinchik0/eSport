package com.rinchik.esport.controller;

import com.rinchik.esport.dto.attendance.AttendanceCreatingRequest;
import com.rinchik.esport.dto.attendance.AttendanceInfoResponse;
import com.rinchik.esport.mapper.AttendanceMapper;
import com.rinchik.esport.model.TrainingAttendance;
import com.rinchik.esport.service.AttendanceService;
import com.rinchik.esport.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_CAPTAIN')")
@RequestMapping("/api/events/trainings/attendance")
public class AttendanceController {
    private final UserService userService;
    private final AttendanceService attService;
    private final AttendanceMapper attMapper;

    @GetMapping
    public ResponseEntity<List<AttendanceInfoResponse>> getTeamAttendance(@AuthenticationPrincipal UserDetails details) {
        List<TrainingAttendance> atts = attService.findAttendanceByTeam(userService.getCurrentUser(details).getTeam().getId());
        List<AttendanceInfoResponse> dtos = new ArrayList<>();
        for (TrainingAttendance a : atts)
            dtos.add(attMapper.toAttendanceInfoResponse(a));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<AttendanceInfoResponse>> getAttendanceByUser(@AuthenticationPrincipal UserDetails details,
                                                                            @PathVariable Long userId) {
        List<TrainingAttendance> atts = attService.findTrainingAttendanceByUser(userId,
                userService.getCurrentUser(details).getId());
        List<AttendanceInfoResponse> dtos = new ArrayList<>();
        for (TrainingAttendance a : atts)
            dtos.add(attMapper.toAttendanceInfoResponse(a));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @PostMapping("/{eventId}/attendance/{userId}")
    public ResponseEntity<Void> noteAttendance(@AuthenticationPrincipal UserDetails details,
                                                @PathVariable Long eventId,
                                                @PathVariable Long userId,
                                                @Valid @RequestBody AttendanceCreatingRequest dto) {
        attService.noteAttendanceByCaptain(userId, eventId, dto.isAttended(), userService.getCurrentUser(details));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
