package com.rinchik.esport.repository;

import com.rinchik.esport.model.methodologyblock.MethodologyBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MethodologyBlockRepository extends JpaRepository<MethodologyBlock, Long> {
    Optional<MethodologyBlock> findById(Long id);
}
