package com.rinchik.esport.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArticleRequestDto {
    @NotBlank
    private String name;
    
    private String text;
    private Long authorId;
}
