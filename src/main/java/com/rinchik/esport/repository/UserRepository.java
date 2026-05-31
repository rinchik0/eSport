package com.rinchik.esport.repository;

import com.rinchik.esport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
}
