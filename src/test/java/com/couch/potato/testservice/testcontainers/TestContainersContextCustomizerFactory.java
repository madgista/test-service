package com.couch.potato.testservice.testcontainers;

import com.couch.potato.testservice.testcontainers.impl.ArtemisContainerFactory;
import com.couch.potato.testservice.testcontainers.impl.KafkaContainerFactory;
import com.couch.potato.testservice.testcontainers.impl.PostgresqlContainerFactory;
import com.couch.potato.testservice.testcontainers.impl.RedisContainerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.MapPropertySource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;

public class TestContainersContextCustomizerFactory implements ContextCustomizerFactory {

    private static final Map<String, ContainerFactory<?>> CONTAINER_FACTORIES = Map.of(
        ContainerName.POSTGRESQL, new PostgresqlContainerFactory(),
        ContainerName.REDIS, new RedisContainerFactory(),
        ContainerName.KAFKA, new KafkaContainerFactory(),
        ContainerName.ARTEMIS, new ArtemisContainerFactory()
    );

    @Nullable
    @Override
    public ContextCustomizer createContextCustomizer(@NonNull Class<?> testClass, @NonNull List<ContextConfigurationAttributes> configAttributes) {
        var annotation = AnnotationUtils.findAnnotation(testClass, EnableTestContainers.class);
        if (annotation == null) {
            return null;
        }
        var containerNames = Arrays.stream(annotation.value()).toList();
        return new ContainerFactoriesContextCustomizer(containerNames);
    }

    private record ContainerFactoriesContextCustomizer(List<String> containerNames) implements ContextCustomizer {

        @Override
        public void customizeContext(@NonNull ConfigurableApplicationContext context, @NonNull MergedContextConfiguration mergedConfig) {
            CONTAINER_FACTORIES.entrySet()
                .stream()
                .filter(entry -> containerNames.isEmpty() || containerNames.contains(entry.getKey()))
                .forEach(entry -> {
                    var propertySource = startContainerAndGetProperties(entry.getKey(), entry.getValue());
                    context.getEnvironment().getPropertySources().addFirst(propertySource);
                });
        }

        private static MapPropertySource startContainerAndGetProperties(String containerName, ContainerFactory<?> containerFactory) {
            containerFactory.getContainer().start();
            var properties = containerFactory.getProperties();
            System.out.printf("Test Properties for : '%s' -> %s%n", containerName, properties);
            return new MapPropertySource("Test Properties for %s".formatted(containerName), properties);
        }
    }
}
