package com.yvolabs.streamapi.service;

import com.yvolabs.streamapi.dto.MovieDto;
import com.yvolabs.streamapi.exception.InvalidObjectIdException;
import com.yvolabs.streamapi.exception.ObjectNotFoundException;
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
        return movieRepository.findById(convertStringToObjectId(movieId))
                .orElseThrow(() -> new ObjectNotFoundException("movie", movieId));

    }

    @Override
    public Movie update(String movieId, MovieDto movieDto) {
        return movieRepository.findById(convertStringToObjectId(movieId))
                .map((movie) -> {
                    Movie movieUpdate = MovieMapper.INSTANCE.updateMovieDto(movieDto, movie);
                    return movieRepository.save(movieUpdate);
                })
                .orElseThrow(() -> new ObjectNotFoundException("movie", movieId));
    }

    @Override
    public void delete(String movieId) {
        Movie movie = movieRepository.findById(convertStringToObjectId(movieId))
                .orElseThrow(() -> new ObjectNotFoundException("movie", movieId));
        movieRepository.delete(movie);
    }

    private static ObjectId convertStringToObjectId(String userId) {
        ObjectId userIdToObjectId;

        try {
            userIdToObjectId = new ObjectId(userId);
        } catch (IllegalArgumentException e) {
            throw new InvalidObjectIdException("movie", userId);
        }

        return userIdToObjectId;
    }

}
