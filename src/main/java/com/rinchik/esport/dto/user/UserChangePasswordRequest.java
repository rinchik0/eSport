package com.rinchik.esport.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserChangePasswordRequest {
    @NotBlank(message = "Old password can not be empty")
    private String oldPassword;

    @NotBlank(message = "New Password can not be empty")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}
