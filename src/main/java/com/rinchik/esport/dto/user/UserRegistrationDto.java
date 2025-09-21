package com.rinchik.esport.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationDto {
    @NotBlank
    private String login;

    @NotBlank
    private String password;

    @NotBlank
    private String email;
}
