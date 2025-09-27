package com.rinchik.esport.service;

import com.rinchik.esport.exception.UserNotFoundException;
import com.rinchik.esport.model.User;
import com.rinchik.esport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final UserRepository userRepo;

    public List<GrantedAuthority> getAuthorities(String login) {
        User user = userRepo.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException());
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }
}
