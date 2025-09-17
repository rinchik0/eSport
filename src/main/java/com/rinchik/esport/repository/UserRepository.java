package com.rinchik.esport.repository;

import com.rinchik.esport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
