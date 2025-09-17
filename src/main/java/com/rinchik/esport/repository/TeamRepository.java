package com.rinchik.esport.repository;

import com.rinchik.esport.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
