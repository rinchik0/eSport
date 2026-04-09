package com.rinchik.esport.mapper;

import com.rinchik.esport.dto.article.ArticleInfoResponse;
import com.rinchik.esport.model.Article;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {
    public ArticleInfoResponse toArticleInfoResponse(Article article) {
        ArticleInfoResponse dto = new ArticleInfoResponse();
        dto.setId(article.getId());
        dto.setName(article.getName());
        dto.setText(article.getText());
        dto.setAuthorName(article.getAuthor().getLogin());
        return dto;
    }
}
