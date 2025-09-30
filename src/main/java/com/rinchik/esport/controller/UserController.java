package com.rinchik.esport.controller;

import com.rinchik.esport.dto.user.UserChangePasswordRequest;
import com.rinchik.esport.dto.user.UserChangesRequest;
import com.rinchik.esport.dto.user.UserInfoResponse;
import com.rinchik.esport.exception.InvalidPasswordException;
import com.rinchik.esport.exception.LoginAlreadyTakenException;
import com.rinchik.esport.exception.UserNotFoundException;
import com.rinchik.esport.mapper.UserMapper;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        List<User> users = service.findAllUsers();
        List<UserInfoResponse> userDtos = new ArrayList<>();
        for (var user : users)
            userDtos.add(mapper.toUserInfoResponse(user));
        return ResponseEntity.status(HttpStatus.OK).body(userDtos);
    }

    @GetMapping("/id_{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = service.findUserById(id);
            return ResponseEntity.status(HttpStatus.OK).body(mapper.toUserInfoResponse(user));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/change_me")
    public ResponseEntity<?> updateCurrentUser(@AuthenticationPrincipal UserDetails details,
                                               @RequestBody UserChangesRequest dto) {
        try {
            User user = service.findUserByLogin(details.getUsername());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(mapper.toUserInfoResponse(service.updateUser(user.getId(), dto)));
        } catch (LoginAlreadyTakenException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/change_password")
    public ResponseEntity<String> changeCurrentUserPassword(@AuthenticationPrincipal UserDetails details,
                                                       @RequestBody UserChangePasswordRequest dto) {
        try {
            User user = service.findUserByLogin(details.getUsername());
            service.changePassword(user.getId(), dto.getOldPassword(), dto.getNewPassword());
            return ResponseEntity.status(HttpStatus.OK).body("Successfully changed");
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/make_admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> makeUserAdmin(@RequestBody Long userId) {
        try {
            service.addSystemRole(userId, SystemRole.ROLE_ADMIN);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully gave role ADMIN");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/unmake_admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> unmakeUserAdmin(@RequestBody Long userId) {
        try {
            service.deleteSystemRole(userId, SystemRole.ROLE_ADMIN);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully cancelled role ADMIN");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/delete_user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@RequestBody Long userId) {
        try {
            service.deleteUser(userId);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
