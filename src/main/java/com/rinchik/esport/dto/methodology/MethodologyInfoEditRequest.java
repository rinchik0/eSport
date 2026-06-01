package com.rinchik.esport.dto.methodology;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rinchik.esport.model.enums.MethodologyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MethodologyInfoEditRequest {
    @NotBlank(message = "Title of methodology can not be empty")
    @Size(min = 3, max = 100, message = "Title of methodology must be between 3 and 100 characters")
    private String title;

    private String description;

    @JsonProperty("image_url")
    private String imageUrl;

    private String duration;
    private String category;

    @NotNull(message = "Level of methodology can not be empty")
    private MethodologyLevel level;
}
