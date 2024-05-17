package com.yvolabs.streamapi.util;

import com.yvolabs.streamapi.model.Movie;
import com.yvolabs.streamapi.model.StreamUser;
import com.yvolabs.streamapi.repository.MovieRepository;
import com.yvolabs.streamapi.repository.UserRepository;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * @author Yvonne N
 */

public class DbInitializeTestData {


    public static void seedData(MovieRepository movieRepo, UserRepository userRepo) {

            movieRepo.deleteAll();
            Movie movie1 = Movie.builder()
                    .id(new ObjectId("663fed2ac3bb554bca098c59"))
                    .title("some-title")
                    .description("some-description")
                    .releaseDate("01-01-2020")
                    .genres(List.of("Genre 1", "Genre 2"))
                    .build();
            Movie movie2 = Movie.builder()
                    .id(new ObjectId("663fed2ac3bb554bca098c58"))
                    .title("some-title-2")
                    .description("some-description")
                    .releaseDate("01-01-2020")
                    .genres(List.of("Genre 1", "Genre 2"))
                    .build();
            movieRepo.save(movie1);
            movieRepo.save(movie2);

            userRepo.deleteAll();
            StreamUser user1 = StreamUser.builder()
                    .id(new ObjectId("663fed2ac3bb554bca098c58"))
                    .firstName("john")
                    .lastName("doe")
                    .email("john@test.com")
                    .password("$2a$10$mT4Z63tHhnjAVXzISH2JLemjlXhdBpQ7S38ehtESTzOi4ExQ1jf0O")
                    .enabled(true)
                    .roles("admin user")
                    .build();
            StreamUser user2 = StreamUser.builder()
                    .id(new ObjectId("6641181ad9650d562fa633ab"))
                    .firstName("jane")
                    .lastName("doe")
                    .email("jane@test.com")
                    .password("$2a$10$hhYyn5mvXsVibiit90CMvenXxmLkZaBLJx1NG/PQbX0XKOwEDBlDC")
                    .enabled(true)
                    .roles("user")
                    .build();
            StreamUser user3 = StreamUser.builder()
                    .id(new ObjectId("6641181ad9650d562fa633ac"))
                    .firstName("sam")
                    .lastName("smith")
                    .email("sam@test.com")
                    .password("$2a$10$hhYyn5mvXsVibiit90CMvenXxmLkZaBLJx1NG/PQbX0XKOwEDBlDC")
                    .enabled(false)
                    .roles("user")
                    .build();
            userRepo.save(user1);
            userRepo.save(user2);
            userRepo.save(user3);


    }
}
