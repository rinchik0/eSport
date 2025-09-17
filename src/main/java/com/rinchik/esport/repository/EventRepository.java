package com.rinchik.esport.repository;

import com.rinchik.esport.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
