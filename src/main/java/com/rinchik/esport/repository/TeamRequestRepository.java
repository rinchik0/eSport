package com.rinchik.esport.repository;

import com.rinchik.esport.model.Rates;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.TeamRequest;
import com.rinchik.esport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRequestRepository extends JpaRepository<TeamRequest, Long> {
    List<TeamRequest> findByTeam(Team team);
    List<TeamRequest> findByUser(User user);
    Optional<TeamRequest> findById(Long id);
}
