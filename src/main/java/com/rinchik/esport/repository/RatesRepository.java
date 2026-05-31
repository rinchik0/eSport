package com.rinchik.esport.repository;

import com.rinchik.esport.model.Rates;
import com.rinchik.esport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatesRepository extends JpaRepository<Rates, Long> {
    Optional<Rates> findByUser(User user);
}
