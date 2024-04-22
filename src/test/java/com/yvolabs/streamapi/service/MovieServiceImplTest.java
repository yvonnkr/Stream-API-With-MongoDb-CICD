package com.yvolabs.streamapi.service;

import com.yvolabs.streamapi.dto.MovieDto;
import com.yvolabs.streamapi.exception.MovieNotFoundException;
import com.yvolabs.streamapi.mapper.MovieMapper;
import com.yvolabs.streamapi.model.Movie;
import com.yvolabs.streamapi.repository.MovieRepository;
import com.yvolabs.streamapi.utils.MovieTestData;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

/**
 * @author Yvonne N
 */
@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {
    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    private List<Movie> movies;

    @BeforeEach
    void setUp() {
        movies = MovieTestData.setMoviesTestData();
    }

    @Test
    void testFindAllSuccess() {
        given(movieRepository.findAll()).willReturn(movies);

        List<Movie> foundMovies = movieService.findAll();

        assertNotNull(foundMovies);
        assertEquals(foundMovies.size(), movies.size());
        verify(movieRepository).findAll();
    }

    @Test
    void testAddSuccess() {
        Movie newMovie = Movie.builder()
                .id(new ObjectId())
                .title("Title 1")
                .description("Description 1")
                .releaseDate("01-01-2020")
                .genres(List.of("Genre 1", "Genre 2"))
                .reviewsIds(null)
                .build();

        given(movieRepository.save(newMovie)).willReturn(newMovie);

        Movie addedMovie = movieService.add(newMovie);

        assertNotNull(addedMovie);
        assertEquals(addedMovie.getId(), newMovie.getId());
        assertEquals(addedMovie.getTitle(), newMovie.getTitle());
        assertEquals(addedMovie.getDescription(), newMovie.getDescription());
        assertEquals(addedMovie.getReleaseDate(), newMovie.getReleaseDate());
        assertEquals(addedMovie.getGenres(), newMovie.getGenres());
        assertEquals(addedMovie.getReviewsIds(), newMovie.getReviewsIds());
        verify(movieRepository).save(newMovie);
    }

    @Test
    void testFindByIdSuccess() {
        String movieId = "662329256487b26751b3d406";

        given(movieRepository.findById(new ObjectId(movieId))).willReturn(Optional.of(movies.get(0)));

        Movie foundMovie = movieService.findById(movieId);

        assertNotNull(foundMovie);
        assertEquals(foundMovie.getId().toString(), movieId);
        assertEquals(foundMovie.getTitle(), movies.get(0).getTitle());
        assertEquals(foundMovie.getDescription(), movies.get(0).getDescription());
        assertEquals(foundMovie.getReleaseDate(), movies.get(0).getReleaseDate());
        assertEquals(foundMovie.getGenres(), movies.get(0).getGenres());
        assertEquals(foundMovie.getReviewsIds(), movies.get(0).getReviewsIds());
        verify(movieRepository).findById(new ObjectId(movieId));
    }

    @Test
    void testFindByIdThrowsNotFoundException() {
        String movieId = "662329256487b26751b3d406";
        given(movieRepository.findById(Mockito.any(ObjectId.class))).willReturn(Optional.empty());


        Throwable throwable = catchThrowable(() ->
                movieService.findById(movieId)
        );

        assertThat(throwable)
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("Could not find movie with Id " + movieId);
        verify(movieRepository).findById(Mockito.any(ObjectId.class));
    }

    @Test
    void testUpdateSuccess() {
        String movieId = "662329256487b26751b3d406";

        Movie foundMovie = movies.get(0);

        MovieDto movieUpdateDto = MovieDto.builder()
                .title("Title Updated")
                .description("Description Updated")
                .releaseDate("01-01-2021")
                .build();

        Movie movieUpdateMapped = MovieMapper.INSTANCE.updateMovieDto(movieUpdateDto, foundMovie);

        Movie movieUpdated = Movie.builder()
                .id(new ObjectId("662329256487b26751b3d406"))
                .title("Title Updated")
                .description("Description Updated")
                .releaseDate("01-01-2021")
                .genres(List.of("Genre 1", "Genre 2"))
                .reviewsIds(null)
                .build();


        given(movieRepository.findById(new ObjectId(movieId))).willReturn(Optional.of(foundMovie));
        given(movieRepository.save(movieUpdateMapped)).willReturn(movieUpdated);

        Movie updatedMovie = movieService.update(movieId, movieUpdateDto);

        assertNotNull(updatedMovie);
        assertEquals(updatedMovie.getId().toString(), movieId);
        assertEquals(updatedMovie.getTitle(), movieUpdated.getTitle());
        assertEquals(updatedMovie.getDescription(), movieUpdated.getDescription());
        assertEquals(updatedMovie.getReleaseDate(), movieUpdated.getReleaseDate());
        assertEquals(updatedMovie.getGenres(), movieUpdated.getGenres());
        assertEquals(updatedMovie.getReviewsIds(), movieUpdated.getReviewsIds());
        verify(movieRepository).findById(new ObjectId(movieId));
        verify(movieRepository).save(movieUpdateMapped);
    }

    @Test
    void testUpdateThrowsNotFoundException() {
        String movieId = "662329256487b26751b3d406";
        given(movieRepository.findById(Mockito.any(ObjectId.class))).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() ->
                movieService.update(movieId, MovieDto.builder().build())
        );

        assertThat(throwable).isInstanceOf(MovieNotFoundException.class);
        verify(movieRepository).findById(Mockito.any(ObjectId.class));
    }

    @Test
    void testDeleteSuccess() {
        Movie movie = movies.get(0);
        given(movieRepository.findById(movie.getId())).willReturn(Optional.of(movie));
        doNothing().when(movieRepository).delete(movie);

        movieService.delete(movie.getId().toString());
        verify(movieRepository).delete(movie);

    }

    @Test
    void testDeleteThrowsNotFoundException() {
        Movie movie = movies.get(0);
        given(movieRepository.findById(movie.getId())).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> movieService.delete(movie.getId().toString()));
        assertThat(throwable).isInstanceOf(MovieNotFoundException.class);
        verify(movieRepository).findById(movie.getId());

    }

}