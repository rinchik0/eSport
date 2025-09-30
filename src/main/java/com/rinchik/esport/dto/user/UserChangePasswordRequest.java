package com.rinchik.esport.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserChangePasswordRequest {
    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
