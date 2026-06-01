package com.rinchik.esport.controller;

import com.rinchik.esport.dto.faceit.FaceitConnectRequest;
import com.rinchik.esport.dto.user.LoginResponse;
import com.rinchik.esport.dto.user.UserInfoResponse;
import com.rinchik.esport.dto.user.UserLoginRequest;
import com.rinchik.esport.dto.user.UserRegistrationRequest;
import com.rinchik.esport.mapper.UserMapper;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.service.FaceitService;
import com.rinchik.esport.service.JwtTokenService;
import com.rinchik.esport.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtTokenService jwtService;
    private final FaceitService faceitService;
    private final UserMapper mapper;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> registerUser(@Valid @RequestBody UserRegistrationRequest dto) {
        User user = userService.registerNewUser(dto);
        userService.addSystemRole(user.getId(), SystemRole.ROLE_GUEST);
        String token = jwtService.generateToken(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toLoginResponse(user, token));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody UserLoginRequest dto) {
        User user = userService.loginUser(dto);
        String token = jwtService.generateToken(user.getId());
        LoginResponse response = mapper.toLoginResponse(user, token);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal UserDetails details) {
        User user = userService.getCurrentUser(details);
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toUserInfoResponse(user));
    }

    @PutMapping("/me/connect_faceit")
    public ResponseEntity<?> connectFaceitAccount(@AuthenticationPrincipal UserDetails details,
                                                  @Valid @RequestBody FaceitConnectRequest dto) {
        User user = userService.getCurrentUser(details);
        faceitService.connectUserToFaceit(user, dto);
        return ResponseEntity.status(HttpStatus.OK).body("FACEIT аккаунт успешно подключен");
    }
}
