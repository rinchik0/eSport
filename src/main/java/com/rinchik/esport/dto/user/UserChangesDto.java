package com.rinchik.esport.dto.user;

import lombok.Data;

@Data
public class UserChangesDto {
    private Long id;
    private String login;
    private String email;
    private String description;
}
