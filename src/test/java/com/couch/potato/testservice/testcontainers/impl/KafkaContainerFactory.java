package com.couch.potato.testservice.testcontainers.impl;

import com.couch.potato.testservice.testcontainers.ContainerFactory;
import com.couch.potato.testservice.testcontainers.ContainerName;
import java.util.Map;
import lombok.Getter;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.DockerImageName;

@Getter
public class KafkaContainerFactory implements ContainerFactory<KafkaContainer> {

    private static final String IMAGE_NAME = "confluentinc/cp-kafka:7.5.0";
    private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse(IMAGE_NAME).asCompatibleSubstituteFor(ContainerName.KAFKA);

    private final KafkaContainer container;

    public KafkaContainerFactory() {
        container = new KafkaContainer(DOCKER_IMAGE_NAME)
            .withReuse(true)
            .withExposedPorts(9092, 9093)
            .withImagePullPolicy(PullPolicy.defaultPolicy())
            .waitingFor(Wait.forListeningPort());
    }

    @Override
    public Map<String, Object> getProperties() {
        return Map.of(
            "spring.kafka.bootstrap-servers", container.getBootstrapServers()
        );
    }
}
