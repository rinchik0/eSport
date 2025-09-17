package com.rinchik.esport.repository;

import com.rinchik.esport.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
