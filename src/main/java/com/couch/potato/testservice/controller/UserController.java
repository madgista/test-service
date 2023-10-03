package com.couch.potato.testservice.controller;

import com.couch.potato.testservice.data.service.UserService;
import com.couch.potato.testservice.dto.UserDto;
import com.couch.potato.testservice.exceptions.UserNotFound;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/user/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable @NotBlank String username) {
        return userService.findUserByUsername(username)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new UserNotFound("Cannot found any user by username -> %s".formatted(username)));
    }
}
