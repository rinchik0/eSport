package com.rinchik.esport.dto;

import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDto {
    @NotBlank
    private String login;

    @NotBlank
    private String password;

    private String name;
    private SystemRole systemRole;
}
