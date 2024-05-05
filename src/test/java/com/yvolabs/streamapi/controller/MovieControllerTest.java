package com.yvolabs.streamapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yvolabs.streamapi.dto.MovieDto;
import com.yvolabs.streamapi.exception.ObjectNotFoundException;
import com.yvolabs.streamapi.model.Movie;
import com.yvolabs.streamapi.response.StatusCode;
import com.yvolabs.streamapi.service.MovieService;
import com.yvolabs.streamapi.utils.MovieTestData;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author Yvonne N
 */
@WebMvcTest(controllers = MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}/movies")
    private String PATH;

    List<Movie> movies;

    @BeforeEach
    void setUp() {
        movies = MovieTestData.setMoviesTestData();
    }

    @Test
    void testGetAllMovies() throws Exception {
        given(movieService.findAll()).willReturn(movies);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(movies.size())));
        verify(movieService).findAll();
    }

    @Test
    void testAddMovieSuccess() throws Exception {
        MovieDto newMovie = MovieDto.builder()
                .title("Title 1")
                .description("Description 1")
                .releaseDate("01-01-2020")
                .genres(List.of("Genre 1", "Genre 2"))
                .build();

        String newMovieJson = objectMapper.writeValueAsString(newMovie);

        Movie savedMovie = Movie.builder()
                .id(new ObjectId())
                .title("Title 1")
                .description("Description 1")
                .releaseDate("01-01-2020")
                .genres(List.of("Genre 1", "Genre 2"))
                .build();

        given(movieService.add(Mockito.any(Movie.class))).willReturn(savedMovie);

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newMovieJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.title").value(savedMovie.getTitle()))
                .andExpect(jsonPath("$.data.description").value(savedMovie.getDescription()))
                .andExpect(jsonPath("$.data.releaseDate").value(savedMovie.getReleaseDate()))
                .andExpect(jsonPath("$.data.genres", Matchers.hasSize(2)));

        verify(movieService).add(Mockito.any(Movie.class));
    }

    @Test
    void testAddMovieFieldNotEmptyValidation() throws Exception {
        MovieDto newMovie = MovieDto.builder()
                .build();

        String newMovieJson = objectMapper.writeValueAsString(newMovie);

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newMovieJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are not valid, see data for details"))
                .andExpect(jsonPath("$.data.title").value("title is required"))
                .andExpect(jsonPath("$.data.description").value("description is required"))
                .andExpect(jsonPath("$.data.releaseDate").value("releaseDate is required"));

        verify(movieService, times(0)).add(Mockito.any(Movie.class));
    }

    @Test
    void testAddMovieFieldSizeValidation() throws Exception {
        MovieDto newMovie = MovieDto.builder()
                .title("t")
                .description("d")
                .releaseDate("r")
                .genres(List.of("Genre 1", "Genre 2"))
                .build();

        String newMovieJson = objectMapper.writeValueAsString(newMovie);

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newMovieJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are not valid, see data for details"))
                .andExpect(jsonPath("$.data.title").value("title length must be at least 3"))
                .andExpect(jsonPath("$.data.description").value("description length must be at least 3"))
                .andExpect(jsonPath("$.data.releaseDate").value("releaseDate length must be at least 3"));

        verify(movieService, times(0)).add(Mockito.any(Movie.class));
    }


    @Test
    void testGetMovieByIdSuccess() throws Exception {
        String movieId = "662329256487b26751b3d406";
        given(movieService.findById(movieId)).willReturn(movies.get(0));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/" + movieId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.title").value("Title 1"))
                .andExpect(jsonPath("$.data.description").value("Description 1"))
                .andExpect(jsonPath("$.data.releaseDate").value("01-01-2020"))
                .andExpect(jsonPath("$.data.genres", Matchers.hasSize(2)));

        verify(movieService).findById(movieId);
    }

    @Test
    void testGetMovieByIdNotfound() throws Exception {
        String movieId = "662329256487b26751b3d406";
        given(movieService.findById(movieId)).willThrow(new ObjectNotFoundException("movie", movieId));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/" + movieId).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find movie with id " + movieId));

        verify(movieService).findById(movieId);
    }

    @Test
    void testUpdateMovieSuccess() throws Exception {
        String movieId = "662329256487b26751b3d406";

        MovieDto movieUpdateDto = MovieDto.builder()
                .title("Title Updated")
                .description("Description Updated")
                .releaseDate("01-01-2021")
                .build();

        String movieUpdateDtoJson = objectMapper.writeValueAsString(movieUpdateDto);

        Movie updatedMovie = Movie.builder()
                .id(new ObjectId("662329256487b26751b3d406"))
                .title("Title Updated")
                .description("Description Updated")
                .releaseDate("01-01-2021")
                .genres(List.of("Genre 1", "Genre 2"))
                .reviewsIds(null)
                .build();

        given(movieService.update(movieId, movieUpdateDto)).willReturn(updatedMovie);

        mockMvc.perform(
                        patch(PATH + "/" + movieId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(movieUpdateDtoJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.title").value("Title Updated"))
                .andExpect(jsonPath("$.data.description").value("Description Updated"))
                .andExpect(jsonPath("$.data.releaseDate").value("01-01-2021"))
                .andExpect(jsonPath("$.data.genres", Matchers.hasSize(2)));
        verify(movieService).update(movieId, movieUpdateDto);

    }

    @Test
    void testUpdateMovieNotfound() throws Exception {
        String movieId = "662329256487b26751b3d406";
        MovieDto movieUpdateDto = MovieDto.builder()
                .title("Title Updated")
                .description("Description Updated")
                .releaseDate("01-01-2021")
                .build();

        String movieUpdateDtoJson = objectMapper.writeValueAsString(movieUpdateDto);

        given(movieService.update(movieId, movieUpdateDto))
                .willThrow(new ObjectNotFoundException("movie", movieId));

        mockMvc.perform(
                        patch(PATH + "/" + movieId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(movieUpdateDtoJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find movie with id " + movieId))
                .andExpect(jsonPath("$.data").isEmpty());
        verify(movieService).update(movieId, movieUpdateDto);
    }

    @Test
    void testUpdateMovieFieldValidation() throws Exception {
        String movieId = "662329256487b26751b3d406";

        MovieDto movieUpdateDto = MovieDto.builder()
                .title("")
                .description("d")
                .releaseDate("01")
                .build();

        String movieUpdateDtoJson = objectMapper.writeValueAsString(movieUpdateDto);

        mockMvc.perform(
                        patch(PATH + "/" + movieId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(movieUpdateDtoJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are not valid, see data for details"))
                .andExpect(jsonPath("$.data.title").value("title is required and length must be at least 3, alternatively please remove this field"))
                .andExpect(jsonPath("$.data.description").value("description is required and length must be at least 3, alternatively please remove this field"))
                .andExpect(jsonPath("$.data.releaseDate").value("releaseDate is required and length must be at least 3, alternatively please remove this field"));
    }

    @Test
    void testUpdateMovieDoNotThrowIfFieldIsNull() throws Exception {
        String movieId = "662329256487b26751b3d406";

        //empty requestBody / field should be validated only if not null
        MovieDto movieUpdateDto = MovieDto.builder()
                .build();

        String movieUpdateDtoJson = objectMapper.writeValueAsString(movieUpdateDto);

        Movie updatedMovie = Movie.builder()
                .id(new ObjectId("662329256487b26751b3d406"))
                .title("Title Updated")
                .description("Description Updated")
                .releaseDate("01-01-2021")
                .genres(List.of("Genre 1", "Genre 2"))
                .reviewsIds(null)
                .build();

        given(movieService.update(movieId, movieUpdateDto)).willReturn(updatedMovie);

        mockMvc.perform(
                        patch(PATH + "/" + movieId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(movieUpdateDtoJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.title").value("Title Updated"))
                .andExpect(jsonPath("$.data.description").value("Description Updated"))
                .andExpect(jsonPath("$.data.releaseDate").value("01-01-2021"))
                .andExpect(jsonPath("$.data.genres", Matchers.hasSize(2)));
        verify(movieService).update(movieId, movieUpdateDto);

    }

    @Test
    void testDeleteMovieSuccess() throws Exception {
        String movieId = "662329256487b26751b3d406";
        doNothing().when(movieService).delete(movieId);
        mockMvc.perform(delete(PATH + "/" + movieId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
        verify(movieService).delete(movieId);
    }

    @Test
    void testDeleteMovieNotFoundException() throws Exception {
        String movieId = "662329256487b26751b3d406";
        doThrow(new ObjectNotFoundException("movie", movieId)).when(movieService).delete(movieId);
        mockMvc.perform(delete(PATH + "/" + movieId).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find movie with id " + movieId))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}