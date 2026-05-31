package com.rinchik.esport.repository;

import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.TrainingAttendance;
import com.rinchik.esport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingAttendanceRepository extends JpaRepository<TrainingAttendance, Long> {
    List<TrainingAttendance> findByTraining(Event training);
    List<TrainingAttendance> findByUser(User user);
    List<TrainingAttendance> findAll();

}
