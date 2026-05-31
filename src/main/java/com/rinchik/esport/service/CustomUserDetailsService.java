package com.rinchik.esport.service;

import com.rinchik.esport.exception.UserNotFoundException;
import com.rinchik.esport.model.User;
import com.rinchik.esport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;
    private final Integer updateOnlineIntervalInMinutes = 5;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toList()))
                .build();
    }

    public UserDetails loadUserById(Long id) throws UserNotFoundException {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public void updateLastOnlineIfNeeded(Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user != null) {
            LocalDateTime now = LocalDateTime.now();
            if (user.getLastOnline() == null ||
                    Duration.between(user.getLastOnline(), now).toMinutes() >= updateOnlineIntervalInMinutes) {
                user.setLastOnline(now);
                userRepo.save(user);
            }
        }
    }
}