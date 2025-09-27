package com.rinchik.esport.mapper;

import com.rinchik.esport.dto.user.LoginResponse;
import com.rinchik.esport.dto.user.UserDetailsDto;
import com.rinchik.esport.dto.user.UserInfoResponse;
import com.rinchik.esport.dto.user.UserRegistrationRequest;
import com.rinchik.esport.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserRegistrationRequest toUserRegistrationResponse(User user) {
        UserRegistrationRequest dto = new UserRegistrationRequest();
        dto.setLogin(user.getLogin());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        return dto;
    }

    public UserInfoResponse toUserInfoResponse(User user) {
        UserInfoResponse dto = new UserInfoResponse();
        dto.setId(user.getId());
        dto.setDescription(user.getDescription());
        dto.setRole(user.getRole());
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

    public UserDetailsDto userDetailsDto(User user) {
        UserDetailsDto dto = new UserDetailsDto();
        dto.setLogin(user.getLogin());
        dto.setRole(user.getRole());
        dto.setEmail(user.getEmail());
        dto.setTeamName(user.getTeam() != null ? user.getTeam().getName() : null);
        dto.setTeamRole(user.getRoleInTeam() != null ? user.getRoleInTeam() : null);
        dto.setDescription(user.getDescription() != null ? user.getDescription() : null);
        return dto;
    }
}
