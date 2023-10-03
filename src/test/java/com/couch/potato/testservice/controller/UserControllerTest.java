package com.couch.potato.testservice.controller;

import com.couch.potato.testservice.data.domain.User;
import com.couch.potato.testservice.data.repository.UserRepository;
import com.couch.potato.testservice.dto.UserDto;
import com.couch.potato.testservice.testcontainers.ContainerName;
import com.couch.potato.testservice.testcontainers.EnableTestContainers;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@Tag("integration-test")
@EnableTestContainers(ContainerName.POSTGRESQL)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository repository;

    @BeforeEach
    void init() {
        repository.deleteAll();
        RestAssured.port = port;
    }

    @AfterAll
    static void finish() {
        RestAssured.reset();
    }

    @Test
    void activateWhenUsernameExists() {
        var username = RandomStringUtils.randomAlphabetic(10);
        var user = new User();
        user.setUsername(username);
        var loaded = repository.save(user);
        Assertions.assertThat(loaded).isNotNull();
        Assertions.assertThat(loaded.getUsername()).isEqualTo(username);

        var path = "/users/user/%s".formatted(username);
        var userDto = RestAssured
            .given()
            .when()
            .get(path)
            .then()
            .log().everything()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response()
            .as(UserDto.class);

        Assertions.assertThat(userDto).isNotNull();
        repository.deleteAll();
    }
}
