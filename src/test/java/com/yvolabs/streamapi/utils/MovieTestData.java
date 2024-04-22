package com.yvolabs.streamapi.utils;

import com.yvolabs.streamapi.model.Movie;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yvonne N
 */
public class MovieTestData {
    public static List<Movie> setMoviesTestData() {
        List<Movie> movies = new ArrayList<>();


        Movie movie1 = Movie.builder()
                .id(new ObjectId("662329256487b26751b3d406"))
                .title("Title 1")
                .description("Description 1")
                .releaseDate("01-01-2020")
                .genres(List.of("Genre 1", "Genre 2"))
                .reviewsIds(null)
                .build();
        Movie movie2 = Movie.builder()
                .id(new ObjectId("662329256487b26751b3d507"))
                .title("Title 2")
                .description("Description 2")
                .releaseDate("01-01-2020")
                .genres(List.of("Genre 1", "Genre 2"))
                .reviewsIds(null)
                .build();

        movies.add(movie1);
        movies.add(movie2);
        return movies;
    }
}
