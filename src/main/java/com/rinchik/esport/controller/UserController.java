package com.rinchik.esport.controller;

import com.rinchik.esport.dto.user.UserInfoResponse;
import com.rinchik.esport.exception.UserNotFoundException;
import com.rinchik.esport.mapper.UserMapper;
import com.rinchik.esport.model.User;
import com.rinchik.esport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final UserMapper mapper;

//    @GetMapping("/all")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
//        List<User> users = service.findAllUsers();
//        List<UserInfoResponse> userDtos = new ArrayList<>();
//        for (var user : users)
//            userDtos.add(mapper.toUserInfoResponse(user));
//        return ResponseEntity.status(HttpStatus.OK).body(userDtos);
//    }

//    @GetMapping("id_{id}")
//    public ResponseEntity<UserInfoResponse> getUserById(@PathVariable Long id) {
//        try {
//            User user = service.findUserById(id);
//            return ResponseEntity.status(HttpStatus.OK).body(mapper.toUserInfoResponse(user));
//        } catch (UserNotFoundException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        }
//    }
}
