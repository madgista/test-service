package com.couch.potato.testservice.controller;

import com.couch.potato.testservice.data.service.UserService;
import com.couch.potato.testservice.dto.UserDto;
import com.couch.potato.testservice.exceptions.UserNotFound;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Slf4j(topic = "logger")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/user/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable @NotBlank String username) {
        log.info("Request started: username -> {}", username);
        var result = userService.findUserByUsername(username)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new UserNotFound("Cannot found any user by username -> %s".formatted(username)));
        log.info("Request finished: result -> {}", result);
        return result;
    }
}
