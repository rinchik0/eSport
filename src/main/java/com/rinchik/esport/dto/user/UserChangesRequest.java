package com.rinchik.esport.dto.user;

import lombok.Data;

@Data
public class UserChangesRequest {
    private String login;
    private String email;
    private String description;
}
