package com.rinchik.esport.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {
    @NotBlank(message = "Login can not be empty")
    private String login;

    @NotBlank(message = "Password can not be empty")
    private String password;
}
