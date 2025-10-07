package com.rinchik.esport.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserChangesRequest {
    @Size(min = 3, max = 50, message = "Login must be between 3 and 50 characters")
    private String login;

    @Email(message = "Email should be valid")
    private String email;

    private String description;
}
