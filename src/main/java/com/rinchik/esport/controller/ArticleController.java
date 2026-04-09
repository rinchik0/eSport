package com.rinchik.esport.controller;

import com.rinchik.esport.dto.article.ArticleCreatingRequest;
import com.rinchik.esport.dto.article.ArticleInfoResponse;
import com.rinchik.esport.mapper.ArticleMapper;
import com.rinchik.esport.model.Article;
import com.rinchik.esport.model.User;
import com.rinchik.esport.service.ArticleService;
import com.rinchik.esport.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;
    private final ArticleMapper mapper;

    @GetMapping("/all_available")
    public ResponseEntity<List<ArticleInfoResponse>> getAll(@AuthenticationPrincipal UserDetails details) {
        List<ArticleInfoResponse> dtos = new ArrayList<>();
        if (userService.getCurrentUser(details).getTeam() != null)
            for (var e : articleService.findArticlesByTeam(userService.getCurrentUser(details).getTeam().getId()))
                dtos.add(mapper.toArticleInfoResponse(e));
        for (var e : articleService.findCommonArticles())
            dtos.add(mapper.toArticleInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleInfoResponse> getArticleById(@PathVariable Long articleId) {
        Article article = articleService.findArticleById(articleId);
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toArticleInfoResponse(article));
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<ArticleInfoResponse> createNewTeamArticle(@AuthenticationPrincipal UserDetails details,
                                                                    @Valid @RequestBody ArticleCreatingRequest dto) {
        User user = userService.getCurrentUser(details);
        Article article = articleService.createNewArticle(user.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toArticleInfoResponse(article));
    }

    @DeleteMapping("/{articleId}")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<Void> deleteTeamArticle(@AuthenticationPrincipal UserDetails details,
                                                  @PathVariable Long articleId) {
        User user = userService.getCurrentUser(details);
        articleService.deleteArticleByAuthor(articleId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}