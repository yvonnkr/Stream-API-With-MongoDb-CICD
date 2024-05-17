package com.yvolabs.streamapi;

import com.yvolabs.streamapi.repository.MovieRepository;
import com.yvolabs.streamapi.repository.UserRepository;
import com.yvolabs.streamapi.util.DbInitializeTestData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class StreamApiApplication {

    public static void main(String[] args) {

        SpringApplication.run(StreamApiApplication.class, args);
    }


    @Profile("integration-test")
    @Bean
    public CommandLineRunner populateTestContainerDB(MovieRepository movieRepo, UserRepository userRepo) {
        return args -> {

            DbInitializeTestData.seedData(movieRepo, userRepo);

        };
    }

}
