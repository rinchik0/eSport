package com.rinchik.esport.controller;

import com.rinchik.esport.dto.user.LoginResponse;
import com.rinchik.esport.dto.user.UserInfoResponse;
import com.rinchik.esport.dto.user.UserLoginRequest;
import com.rinchik.esport.dto.user.UserRegistrationRequest;
import com.rinchik.esport.exception.InvalidPasswordException;
import com.rinchik.esport.exception.LoginAlreadyTakenException;
import com.rinchik.esport.exception.UserNotFoundException;
import com.rinchik.esport.mapper.UserMapper;
import com.rinchik.esport.model.User;
import com.rinchik.esport.service.JwtTokenService;
import com.rinchik.esport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtTokenService jwtService;
    private final UserMapper mapper;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest dto) {
        try {
            User user = userService.registerNewUser(dto);
            String token = jwtService.generateToken(user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toLoginResponse(user, token));
        } catch (LoginAlreadyTakenException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest dto) {
        try {
            User user = userService.loginUser(dto);
            String token = jwtService.generateToken(user.getId());
            LoginResponse response = mapper.toLoginResponse(user, token);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch(UserNotFoundException | InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal UserDetails details) {
        User user = userService.findUserByLogin(details.getUsername());
        return ResponseEntity.ok(mapper.toUserInfoResponse(user));
    }
}
