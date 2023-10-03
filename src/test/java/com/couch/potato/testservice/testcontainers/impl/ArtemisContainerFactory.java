package com.couch.potato.testservice.testcontainers.impl;

import com.couch.potato.testservice.testcontainers.ContainerFactory;
import com.couch.potato.testservice.testcontainers.ContainerName;
import java.util.Map;
import lombok.Getter;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.DockerImageName;

@Getter
public class ArtemisContainerFactory implements ContainerFactory<GenericContainer<?>> {

    private static final String ARTEMIS_USER = "admin";
    private static final String ARTEMIS_PASSWORD = "admin";

    private static final String IMAGE_NAME = "apache/activemq-artemis:2.30.0-alpine";
    private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse(IMAGE_NAME).asCompatibleSubstituteFor(ContainerName.ARTEMIS);

    private final GenericContainer<?> container;

    public ArtemisContainerFactory() {
        container = new GenericContainer<>(DOCKER_IMAGE_NAME)
            .withReuse(true)
            .withEnv("ARTEMIS_USER", ARTEMIS_USER)
            .withEnv("ARTEMIS_PASSWORD", ARTEMIS_PASSWORD)
            .withEnv("EXTRA_ARGS", "--nio")
            .withExposedPorts(61616)
            .withImagePullPolicy(PullPolicy.defaultPolicy())
            .waitingFor(Wait.forListeningPort());
    }

    @Override
    public Map<String, Object> getProperties() {
        var brokerUrl = "tcp://%s:%d".formatted(container.getHost(), container.getFirstMappedPort());
        return Map.of(
            "spring.artemis.broker-url", brokerUrl,
            "spring.artemis.user", ARTEMIS_USER,
            "spring.artemis.password", ARTEMIS_PASSWORD
        );
    }
}
