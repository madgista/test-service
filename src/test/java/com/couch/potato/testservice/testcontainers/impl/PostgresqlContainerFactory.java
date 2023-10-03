package com.couch.potato.testservice.testcontainers.impl;

import com.couch.potato.testservice.testcontainers.ContainerFactory;
import com.couch.potato.testservice.testcontainers.ContainerName;
import java.util.Map;
import lombok.Getter;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.DockerImageName;

@Getter
public class PostgresqlContainerFactory implements ContainerFactory<PostgreSQLContainer<?>> {

    private static final String IMAGE_NAME = "postgres:15.4-alpine";
    private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse(IMAGE_NAME).asCompatibleSubstituteFor(ContainerName.POSTGRESQL);

    private final PostgreSQLContainer<?> container;

    public PostgresqlContainerFactory() {
        container = new PostgreSQLContainer<>(DOCKER_IMAGE_NAME)
            .withReuse(true)
            .withExposedPorts(5432)
            .withDatabaseName("postgres")
            .withUrlParam("currentSchema", "public")
            .withUrlParam("logUnclosedConnections", "true")
            .withInitScript("initial-script.sql")
            .withImagePullPolicy(PullPolicy.defaultPolicy())
            .waitingFor(Wait.forListeningPort());
    }

    @Override
    public Map<String, Object> getProperties() {
        return Map.of(
            "spring.datasource.url", container.getJdbcUrl(),
            "spring.datasource.username", container.getUsername(),
            "spring.datasource.password", container.getPassword(),
            "spring.datasource.driver-class-name", "org.postgresql.Driver",
            "spring.test.database.replace", "NONE"
        );
    }
}
