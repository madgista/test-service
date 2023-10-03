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
public class RedisContainerFactory implements ContainerFactory<GenericContainer<?>> {

    private static final String IMAGE_NAME = "redis:7.2.0-alpine";
    private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse(IMAGE_NAME).asCompatibleSubstituteFor(ContainerName.REDIS);

    private final GenericContainer<?> container;

    public RedisContainerFactory() {
        container = new GenericContainer<>(DOCKER_IMAGE_NAME)
            .withReuse(true)
            .withExposedPorts(6379)
            .withImagePullPolicy(PullPolicy.defaultPolicy())
            .waitingFor(Wait.forListeningPort());
    }

    @Override
    public Map<String, Object> getProperties() {
        return Map.of(
            "spring.redis.host", container.getHost(),
            "spring.redis.port", container.getFirstMappedPort()
        );
    }
}
