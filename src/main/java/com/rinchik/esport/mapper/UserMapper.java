package com.rinchik.esport.mapper;

import com.rinchik.esport.dto.user.LoginResponse;
import com.rinchik.esport.dto.user.UserInfoResponse;
import com.rinchik.esport.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserInfoResponse toUserInfoResponse(User user) {
        UserInfoResponse dto = new UserInfoResponse();
        dto.setId(user.getId());
        dto.setDescription(user.getDescription());
        dto.setRoles(user.getRoles());
        dto.setEmail(user.getEmail());
        if (user.getTeam() != null) {
            dto.setTeamId(user.getTeam().getId());
            dto.setTeamName(user.getTeam().getName());
        }
        else {
            dto.setTeamId(null);
            dto.setTeamName(null);
        }
        dto.setTeamRole(user.getRoleInTeam());
        dto.setLogin(user.getLogin());
        return dto;
    }

    public LoginResponse toLoginResponse(User user, String token) {
        UserInfoResponse info = toUserInfoResponse(user);
        LoginResponse dto = new LoginResponse();
        dto.setUser(info);
        dto.setToken(token);
        return dto;
    }
}
