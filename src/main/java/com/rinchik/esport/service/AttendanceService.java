package com.rinchik.esport.service;

import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.TrainingAttendance;
import com.rinchik.esport.model.User;
import com.rinchik.esport.repository.TrainingAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {
    private final TrainingAttendanceRepository attRepo;
    private final EventService eventService;
    private final UserService userService;

    public List<TrainingAttendance> findTrainingAttendanceByCaptain(Long eventId, User user) {
        Event event = eventService.findEventByIdIfAvailable(eventId, user);
        return attRepo.findByTraining(event);
    }

    public List<TrainingAttendance> findAttendanceByTeam(Long teamId) {
        List<TrainingAttendance> common = attRepo.findAll();
        List<TrainingAttendance> team = new ArrayList<>();
        for (TrainingAttendance t : common)
            if (t.getTraining().getTeam().getId().equals(teamId))
                team.add(t);
        return team;
    }

    public List<TrainingAttendance> findTrainingAttendanceByUser(Long userId) {
        return attRepo.findByUser(userService.findUserById(userId));
    }

    public List<TrainingAttendance> findTrainingAttendanceByUser(Long userId, Long captainId) {
        List<TrainingAttendance> atts = findTrainingAttendanceByUser(userId);
        List<TrainingAttendance> result = new ArrayList<>();
        for (TrainingAttendance a : atts)
            if (a.getTraining().getTeam().getCaptain().getId().equals(captainId))
                result.add(a);
        return result;
    }

    @Transactional
    public void noteAttendanceByCaptain(Long userId, Long eventId, Boolean attended, User captain) {
        Event training = eventService.findEventByIdIfAvailable(eventId, captain);
        TrainingAttendance att = new TrainingAttendance();
        att.setUser(userService.findUserById(userId));
        att.setTraining(training);
        att.setAttended(attended);
        attRepo.save(att);

    }
}
