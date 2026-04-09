package com.rinchik.esport.service;

import com.rinchik.esport.dto.article.ArticleCreatingRequest;
import com.rinchik.esport.exception.EventNotFoundException;
import com.rinchik.esport.exception.NotAuthorOfArticleException;
import com.rinchik.esport.model.Article;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.User;
import com.rinchik.esport.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository articleRepo;
    private final UserService userService;
    private final TeamService teamService;

    public List<Article> findArticlesByTeam(Long teamId) {
        Team team = teamService.findTeamById(teamId);
        User user = team.getMembers().get(0);
        return articleRepo.findByAuthor(user);
    }

    public List<Article> findAllArticles() {
        return articleRepo.findAll();
    }

    public List<Article> findCommonArticles() {
        List<Article> arts = new ArrayList<>();
        for (Article art : findAllArticles())
            if (art.getAuthor().getTeam().equals(null))
                arts.add(art);
        return arts;
    }

    public Article findArticleById(Long id) {
        return articleRepo.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    @Transactional
    public Article createNewArticle(Long authorId, ArticleCreatingRequest dto) {
        Article newArticle = new Article();

        newArticle.setName(dto.getName());
        newArticle.setText(dto.getText() == null ? null : dto.getText());

        newArticle.setAuthor(userService.findUserById(authorId));

        return articleRepo.save(newArticle);
    }

    private boolean isUserAuthorForTeamArticle(Long userId, Long articleId) {
        Article article = findArticleById(articleId);
        return userId.equals(article.getAuthor().getId());
    }

    @Transactional
    public void deleteArticle(Long id) {
        Article article = findArticleById(id);

        article.setAuthor(null);

        articleRepo.delete(article);
    }

    @Transactional
    public void deleteArticleByAuthor(Long articleId, Long userId) {
        if (isUserAuthorForTeamArticle(userId, articleId))
            deleteArticle(articleId);
        else
            throw new NotAuthorOfArticleException(userId, articleId);
    }
}