package com.couch.potato.testservice.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.couch.potato.testservice.data.domain.User;
import com.couch.potato.testservice.data.repository.UserRepository;
import com.couch.potato.testservice.dto.UserDto;
import com.couch.potato.testservice.testcontainers.ContainerName;
import com.couch.potato.testservice.testcontainers.EnableTestContainers;
import io.restassured.RestAssured;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@Tag("integration-test")
@AutoConfigureObservability
@EnableTestContainers(ContainerName.POSTGRESQL)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "logging.level.org.apache.kafka.clients.NetworkClient=OFF")
class UserControllerTest {

    private static final ListAppender<ILoggingEvent> loggerAppender = new ListAppender<>();
    private static final Logger logger = (Logger) LoggerFactory.getLogger("logger");

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository repository;

    @BeforeEach
    void init() {
        repository.deleteAll();
        RestAssured.port = port;

        loggerAppender.start();
        logger.setLevel(Level.ALL);
        logger.addAppender(loggerAppender);
    }

    @AfterAll
    static void finish() {
        RestAssured.reset();
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"INFO", "ERROR", "WARN"})
    void activateWhenUsernameExists(String logLevel) {
        var level = Level.toLevel(logLevel);
        logger.setLevel(level);

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

        Assertions.assertThat(loggerAppender.list).hasSize(1);
        var event = loggerAppender.list.get(0);
        Assertions.assertThat(event.getMDCPropertyMap()).containsKeys("traceId", "spanId");
    }
}
