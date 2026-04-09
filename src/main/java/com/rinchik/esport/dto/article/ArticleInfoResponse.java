package com.rinchik.esport.dto.article;

import com.rinchik.esport.model.User;
import lombok.Data;

@Data
public class ArticleInfoResponse {
    private Long id;
    private String name;
    private String text;
    private String authorName;
}