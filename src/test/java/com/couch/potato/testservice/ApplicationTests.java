package com.couch.potato.testservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.AbstractApplicationContext;

@Order(1)
@SpringBootTest
@Tag("spring-boot-test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApplicationTests {

    @Autowired
    private AbstractApplicationContext applicationContext;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertThat(applicationContext).isNotNull();
    }
}
