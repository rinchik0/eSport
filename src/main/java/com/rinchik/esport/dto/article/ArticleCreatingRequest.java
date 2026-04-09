package com.rinchik.esport.dto.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleCreatingRequest {
    @NotBlank(message = "Name of article can not be empty")
    @Size(min = 3, max = 50, message = "Name of article must be between 3 and 50 characters")
    private String name;

    private String text;
}
