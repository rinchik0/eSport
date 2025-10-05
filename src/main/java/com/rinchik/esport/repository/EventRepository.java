package com.rinchik.esport.repository;

import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAll();
    List<Event> findByGame(Game game);
    Optional<Event> findById(Long id);
    List<Event> findByTeam(Team team);
    List<Event> findByTeamIsNull();
    List<Event> findByOrganizer(User user);
    List<Event> findByParticipantsId(Long userId);
}
