package com.yvolabs.streamapi.service;

import com.yvolabs.streamapi.dto.MovieDto;
import com.yvolabs.streamapi.model.Movie;

import java.util.List;

/**
 * @author Yvonne N
 */
public interface MovieService {
    List<Movie> findAll();

    Movie add(Movie movie);

    Movie findById(String movieId);

    Movie update(String movieId, MovieDto movieDto);

    void delete(String movieId);
}
