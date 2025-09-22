package com.rinchik.esport.repository;

import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.enums.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAll();
    List<Team> findByGame(Game game);
    Optional<Team> findById(Long id);
    boolean existsByName(String name);
    Optional<Team> findByName(String name);
}
