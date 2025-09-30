package com.rinchik.esport.repository;

import com.rinchik.esport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLogin(String login);
    boolean existsById(Long id);
    Optional<User> findById(Long id);
    Optional<User> findByLogin(String login);
    List<User> findAll();
}
