package com.rinchik.esport.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeamResponseDto {
    private Long id;
    private String name;
    private String description;
    private List<String> memberNames = new ArrayList<>();
    private List<Long> memberId = new ArrayList<>();
}
