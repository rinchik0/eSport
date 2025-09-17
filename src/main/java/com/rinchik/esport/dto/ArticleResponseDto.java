package com.rinchik.esport.dto;

import lombok.Data;

@Data
public class ArticleResponseDto {
    private Long id;
    private String name;
    private String authorName;
    private Long authorId;
    private String text;
}
