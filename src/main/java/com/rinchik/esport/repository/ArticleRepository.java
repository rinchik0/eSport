package com.rinchik.esport.repository;

import com.rinchik.esport.model.Article;
import com.rinchik.esport.model.Event;
import com.rinchik.esport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findById(Long id);
    List<Article> findAll();
    List<Article> findByAuthor(User user);
}
