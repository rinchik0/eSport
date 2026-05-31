package com.rinchik.esport.repository;

import com.rinchik.esport.model.Methodology;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MethodologyRepository extends JpaRepository<Methodology, Long> {
    Optional<Methodology> findById(Long id);
    List<Methodology> findAll();
    List<Methodology> findByAuthor(User user);
    List<Methodology> findByTeam(Team team);
}
