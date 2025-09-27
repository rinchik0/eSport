package com.rinchik.esport.dto.user;

import lombok.Data;

@Data
public class LoginResponse {
    private UserInfoResponse user;
    private String token;
}
