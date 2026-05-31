package com.rinchik.esport.dto.methodology;

import com.rinchik.esport.model.enums.MethodologyLevel;
import lombok.Data;

@Data
public class MethodologyInfoResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Long authorId;
    private String authorName;
    private String duration;
    private String category;
    private MethodologyLevel level;
    private Long teamId;
}