package com.yvolabs.streamapi.service;

import com.yvolabs.streamapi.dto.MovieDto;
import com.yvolabs.streamapi.exception.MovieNotFoundException;
import com.yvolabs.streamapi.mapper.MovieMapper;
import com.yvolabs.streamapi.model.Movie;
import com.yvolabs.streamapi.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yvonne N
 */
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public Movie add(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie findById(String movieId) {
        return movieRepository.findById(new ObjectId(movieId))
                .orElseThrow(() -> new MovieNotFoundException(movieId));

    }

    @Override
    public Movie update(String movieId, MovieDto movieDto) {
        return movieRepository.findById(new ObjectId(movieId))
                .map((movie) -> {
                    Movie movieUpdate = MovieMapper.INSTANCE.updateMovieDto(movieDto, movie);
                    return movieRepository.save(movieUpdate);
                })
                .orElseThrow(() -> new MovieNotFoundException(movieId));
    }

    @Override
    public void delete(String movieId) {
        Movie movie = movieRepository.findById(new ObjectId(movieId))
                .orElseThrow(() -> new MovieNotFoundException(movieId));
        movieRepository.delete(movie);
    }
}
