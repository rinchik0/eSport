package com.rinchik.esport.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImageUploadResponse {
    @JsonProperty("image_url")
    private String imageUrl;
}
