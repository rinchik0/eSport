package com.rinchik.esport.controller;

import com.rinchik.esport.dto.user.UserChangePasswordRequest;
import com.rinchik.esport.dto.user.UserChangesRequest;
import com.rinchik.esport.dto.user.UserInfoResponse;
import com.rinchik.esport.mapper.UserMapper;
import com.rinchik.esport.model.User;
import com.rinchik.esport.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final UserMapper mapper;

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponse> getUserById(@PathVariable Long userId) {
        User user = service.findUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toUserInfoResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserInfoResponse> updateCurrentUser(@AuthenticationPrincipal UserDetails details,
                                                              @Valid @RequestBody UserChangesRequest dto) {
        User user = service.getCurrentUser(details);
        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.toUserInfoResponse(service.updateUser(user.getId(), dto)));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticationPrincipal UserDetails details) {
        User user = service.getCurrentUser(details);
        service.deleteUser(user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changeCurrentUserPassword(@AuthenticationPrincipal UserDetails details,
                                                          @Valid @RequestBody UserChangePasswordRequest dto) {
        User user = service.getCurrentUser(details);
        service.changePassword(user.getId(), dto.getOldPassword(), dto.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        List<User> users = service.findAllUsers();
        List<UserInfoResponse> userDtos = new ArrayList<>();
        for (var user : users)
            userDtos.add(mapper.toUserInfoResponse(user));
        return ResponseEntity.status(HttpStatus.OK).body(userDtos);
    }
}
