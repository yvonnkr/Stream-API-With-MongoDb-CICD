package com.yvolabs.streamapi;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;

/**
 * @author Yvonne N
 */
@TestConfiguration(proxyBeanMethods = false)
@ActiveProfiles("integration-test")
@Disabled
public class TestApplicationStarter {
    @Bean
    @ServiceConnection
    MongoDBContainer mongoDbContainer() {
        return new MongoDBContainer("mongo:4.4.6").withReuse(true);
    }

    public static void main(String[] args) {
        // Populated db --> See: StreamApiApplication::populateTestContainerDB
        SpringApplication.from(StreamApiApplication::main)
                .with(TestApplicationStarter.class)
                .run(args);
    }

}
