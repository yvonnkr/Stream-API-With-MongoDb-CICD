package com.yvolabs.streamapi.mapper;

import com.yvolabs.streamapi.dto.MovieDto;
import com.yvolabs.streamapi.model.Movie;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mapstruct.BeforeMapping;

import java.util.List;

import static com.yvolabs.streamapi.mapper.MovieMapper.INSTANCE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Yvonne N
 */
class MovieMapperTest {

    @Test
    void movieToMovieDtoMapper() {
        Movie movie = Movie.builder()
                .id(new ObjectId("662329256487b26751b3d406"))
                .title("some_movie_title")
                .description("some_movie_description")
                .releaseDate("01-02-2020")
                .genres(List.of("genre1", "genre2"))
                .reviewsIds(List.of())
                .build();

        MovieDto movieDto = INSTANCE.movieToMovieDtoMapper(movie);

        assertNotNull(movieDto);
        assertEquals(movieDto.getId(), movie.getId().toString());
        assertEquals(movieDto.getTitle(), "some_movie_title");
        assertEquals(movieDto.getDescription(), "some_movie_description");
        assertEquals(movieDto.getReleaseDate(), "01-02-2020");
        assertEquals(movieDto.getGenres(), List.of("genre1", "genre2"));
        assertEquals(movieDto.getReviewsIds(), List.of());

        assertNull(INSTANCE.movieToMovieDtoMapper(null));
    }


    @Test
    void movieDtoToMovieMapper() {
        MovieDto movieDto = MovieDto.builder()
                .id("662329256487b26751b3d406")
                .title("some_movie_title")
                .description("some_movie_description")
                .releaseDate("01-02-2020")
                .genres(List.of("genre1", "genre2"))
                .reviewsIds(List.of())
                .build();
        Movie movie = INSTANCE.movieDtoToMovieMapper(movieDto);

        assertNotNull(movie);
        assertEquals(movie.getId(), new ObjectId(movieDto.getId()));
        assertEquals(movie.getTitle(), "some_movie_title");
        assertEquals(movie.getDescription(), "some_movie_description");
        assertEquals(movie.getReleaseDate(), "01-02-2020");
        assertEquals(movie.getGenres(), List.of("genre1", "genre2"));
        assertEquals(movie.getReviewsIds(), List.of());

        assertNull(INSTANCE.movieDtoToMovieMapper(null));

    }


    @Test
    @BeforeMapping()
    void updateMovieDto() {

        MovieDto movieDto = MovieDto.builder()
                .id("662329256487b26751b3d406")
                .title("some_movie_title_updated")
                .description("some_movie_description_updated")
                .releaseDate("01-02-2020")
                .build();

        Movie movie = Movie.builder()
                .id(new ObjectId("662329256487b26751b3d406"))
                .title("some_movie_title")
                .description("some_movie_description")
                .releaseDate("01-02-2020")
                .genres(List.of("genre1", "genre2"))
                .reviewsIds(List.of())
                .build();

        Movie updatedMovie = INSTANCE.updateMovieDto(movieDto, movie);

        assertNotNull(updatedMovie);
        assertEquals(updatedMovie.getId(), new ObjectId("662329256487b26751b3d406"));
        assertEquals(updatedMovie.getTitle(), "some_movie_title_updated");
        assertEquals(updatedMovie.getDescription(), "some_movie_description_updated");
        assertEquals(updatedMovie.getReleaseDate(), "01-02-2020");
    }

    @Test
    void objectIdToString() {
        String str = MovieMapper.objectIdToString(new ObjectId("662329256487b26751b3d406"));
        assertNotNull(str);
        assertEquals(str, "662329256487b26751b3d406");
    }

    @Test
    void stringToObjectId() {
        ObjectId objectId = MovieMapper.stringToObjectId("662329256487b26751b3d406");
        assertNotNull(objectId);
        assertEquals(objectId, new ObjectId("662329256487b26751b3d406"));

        assertNull(MovieMapper.stringToObjectId(""));
        assertNull(MovieMapper.stringToObjectId(null));

    }
}