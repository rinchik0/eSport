package com.rinchik.esport.mapper;

import com.rinchik.esport.dto.user.UserDetailsDto;
import com.rinchik.esport.dto.user.UserRegistrationDto;
import com.rinchik.esport.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserRegistrationDto toUserRegistrationDto(User user) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setLogin(user.getLogin());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        return dto;
    }

    public UserDetailsDto userDetailsDto(User user) {
        UserDetailsDto dto = new UserDetailsDto();
        dto.setLogin(user.getLogin());
        dto.setName(user.getName() != null ? user.getName() : null);
        dto.setRole(user.getRole());
        dto.setEmail(user.getEmail());
        dto.setTeamName(user.getTeam() != null ? user.getTeam().getName() : null);
        dto.setTeamRole(user.getRoleInTeam() != null ? user.getRoleInTeam() : null);
        dto.setDescription(user.getDescription() != null ? user.getDescription() : null);
        return dto;
    }
}
