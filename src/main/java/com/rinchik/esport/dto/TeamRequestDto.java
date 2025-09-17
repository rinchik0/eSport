package com.rinchik.esport.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TeamRequestDto {
    @NotBlank
    private String name;

    private String description;
}
