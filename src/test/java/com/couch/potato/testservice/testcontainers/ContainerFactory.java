package com.couch.potato.testservice.testcontainers;

import java.util.Map;
import org.testcontainers.containers.GenericContainer;

public interface ContainerFactory<T extends GenericContainer<?>> {

    T getContainer();

    Map<String, Object> getProperties();
}
