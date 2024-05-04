package com.yvolabs.streamapi.controller;

import com.yvolabs.streamapi.annotations.CreateValidationGroup;
import com.yvolabs.streamapi.annotations.UpdateValidationGroup;
import com.yvolabs.streamapi.dto.MovieDto;
import com.yvolabs.streamapi.model.Movie;
import com.yvolabs.streamapi.response.Result;
import com.yvolabs.streamapi.response.StatusCode;
import com.yvolabs.streamapi.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.yvolabs.streamapi.mapper.MovieMapper.INSTANCE;

/**
 * @author Yvonne N
 */
@RestController
@RequestMapping("${api.endpoint.base-url}/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<Result<List<MovieDto>>> getAllMovies() {
        List<Movie> movies = movieService.findAll();

        List<MovieDto> movieResponseDtos = movies.stream()
                .map(INSTANCE::movieToMovieDtoMapper)
                .toList();


        Result<List<MovieDto>> result = Result.<List<MovieDto>>builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Find All Success")
                .data(movieResponseDtos)
                .build();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Result<MovieDto>> addMovie(@Validated({CreateValidationGroup.class}) @RequestBody MovieDto movieDto) {
        Movie movie = INSTANCE.movieDtoToMovieMapper(movieDto);
        Movie addedMovie = movieService.add(movie);
        MovieDto addedMovieDto = INSTANCE.movieToMovieDtoMapper(addedMovie);

        Result<MovieDto> result = Result.<MovieDto>builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Add Success")
                .data(addedMovieDto)
                .build();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<Result<MovieDto>> getMovieById(@PathVariable String movieId) {
        Movie movie = movieService.findById(movieId);
        MovieDto movieDto = INSTANCE.movieToMovieDtoMapper(movie);

        Result<MovieDto> result = Result.<MovieDto>builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Find One Success")
                .data(movieDto)
                .build();

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{movieId}")
    public ResponseEntity<Result<MovieDto>> updateMovie(
            @PathVariable String movieId,
            @Validated({UpdateValidationGroup.class}) @RequestBody MovieDto movieDto) {
        Movie updatedMovie = movieService.update(movieId, movieDto);
        MovieDto updatedMovieDto = INSTANCE.movieToMovieDtoMapper(updatedMovie);

        Result<MovieDto> result = Result.<MovieDto>builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Update Success")
                .data(updatedMovieDto)
                .build();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Result<Object>> deleteMovieById(@PathVariable String movieId) {
        movieService.delete(movieId);
        return ResponseEntity.ok(
                Result.builder()
                        .flag(true)
                        .code(StatusCode.SUCCESS)
                        .message("Delete Success")
                        .build());

    }
}
