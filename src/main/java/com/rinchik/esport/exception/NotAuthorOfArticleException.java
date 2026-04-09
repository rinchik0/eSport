package com.rinchik.esport.exception;

public class NotAuthorOfArticleException extends RuntimeException {
    public NotAuthorOfArticleException(Long userId, Long articleId) {
        super("User with id " + userId + " is not captain of team with id " + articleId);
    }
}
