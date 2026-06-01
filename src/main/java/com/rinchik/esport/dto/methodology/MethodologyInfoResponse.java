package com.rinchik.esport.dto.methodology;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rinchik.esport.model.enums.MethodologyLevel;
import lombok.Data;

@Data
public class MethodologyInfoResponse {
    private Long id;
    private String title;
    private String description;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("author_name")
    private String authorName;

    private String duration;
    private String category;
    private MethodologyLevel level;

    @JsonProperty("team_id")
    private Long teamId;
}