package com.yvolabs.streamapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yvolabs.streamapi.dto.MovieDto;
import com.yvolabs.streamapi.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author Yvonne N
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for Movies API endpoints")
@Slf4j
@Testcontainers
@ActiveProfiles("integration-test")
@Disabled
public class MovieControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @Value("${api.endpoint.base-url}")
    String BASE_URL;

    @ServiceConnection
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl("test-db"));
    }

    @BeforeEach
    void setUp() throws Exception {
        loginAndSetAuthToken();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindAllMoviesSuccess() throws Exception {
        mockMvc.perform(get(BASE_URL + "/movies").accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(2)));
    }

    @Test
    void testFindMovieByIdSuccess() throws Exception {
        String movieId = "663fed2ac3bb554bca098c59";

        mockMvc.perform(get(BASE_URL + "/movies/" + movieId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.title").value("some-title"))
                .andExpect(jsonPath("$.data.description").value("some-description"));
    }

    @Test
    void testFindMovieByIdThrowsWhenIdIsInvalid() throws Exception {
        String movieId = "123invalidId";

        mockMvc.perform(get(BASE_URL + "/movies/" + movieId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message", Matchers.containsString("movie id: " + movieId + " is invalid")));
    }

    @Test
    void testFindMovieByIdThrowsWhenIdIsNotFound() throws Exception {
        String movieId = "663fed2ac3bb554bca098c50";

        mockMvc.perform(get(BASE_URL + "/movies/" + movieId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message", Matchers.containsString("Could not find movie with id " + movieId)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddMovieSuccess() throws Exception {

        MovieDto movieDto = MovieDto.builder()
                .title("Test Title")
                .description("Test Description")
                .releaseDate("01/01/2020")
                .genres(List.of("Test Genre"))
                .reviewsIds(List.of())
                .build();

        String json = objectMapper.writeValueAsString(movieDto);

        this.mockMvc.perform(post(BASE_URL + "/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.title").value("Test Title"))
                .andExpect(jsonPath("$.data.description").value("Test Description"));

        this.mockMvc.perform(get(BASE_URL + "/movies")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)));
    }

    @Test
    void testAddMovieThrowsWhenInvalidRequestData() throws Exception {

        MovieDto movieDto = MovieDto.builder()
                .title("t")
                .description("d")
                .releaseDate("r")
                .build();

        String json = objectMapper.writeValueAsString(movieDto);

        this.mockMvc.perform(post(BASE_URL + "/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are not valid, see data for details"))
                .andExpect(jsonPath("$.data.title").value("title length must be at least 3"))
                .andExpect(jsonPath("$.data.description").value("description length must be at least 3"))
                .andExpect(jsonPath("$.data.releaseDate").value("releaseDate length must be at least 3"));

        //assert no movie was added
        this.mockMvc.perform(get(BASE_URL + "/movies")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(2)));
    }

    @Test
    void testAddMovieThrowsWhenInvalidAuthToken() throws Exception {

        MovieDto movieDto = MovieDto.builder()
                .title("Test Title")
                .description("Test Description")
                .releaseDate("01/01/2020")
                .genres(List.of("Test Genre"))
                .reviewsIds(List.of())
                .build();

        String json = objectMapper.writeValueAsString(movieDto);

        String invalidToken = getInvalidToken();

        this.mockMvc.perform(post(BASE_URL + "/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, invalidToken))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message", Matchers.containsString("The access token provided is expired, revoked, malformed or invalid for other reasons")));

        //assert no movie was added
        this.mockMvc.perform(get(BASE_URL + "/movies")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(2)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testUpdateMovieSuccess() throws Exception {

        String movieId = "663fed2ac3bb554bca098c58";

        // Before Update Check
        this.mockMvc.perform(get(BASE_URL + "/movies/" + movieId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.title").value("some-title-2"))
                .andExpect(jsonPath("$.data.description").value("some-description"))
                .andExpect(jsonPath("$.data.releaseDate").value("01-01-2020"));

        MovieDto updateMovieDto = MovieDto.builder()
                .title("updated-title")
                .description("updated-description")
                .releaseDate("01-01-2021")
                .build();

        String json = objectMapper.writeValueAsString(updateMovieDto);

        // Update Check
        this.mockMvc.perform(patch(BASE_URL + "/movies/" + movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.title").value("updated-title"))
                .andExpect(jsonPath("$.data.description").value("updated-description"))
                .andExpect(jsonPath("$.data.releaseDate").value("01-01-2021"));

        // After Update Check
        this.mockMvc.perform(get(BASE_URL + "/movies/" + movieId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.title").value("updated-title"))
                .andExpect(jsonPath("$.data.description").value("updated-description"))
                .andExpect(jsonPath("$.data.releaseDate").value("01-01-2021"));
    }

    @Test
    void testUpdateMovieThrowsWhenInvalidRequestData() throws Exception {

        String movieId = "663fed2ac3bb554bca098c58";

        // Before Update Check
        this.mockMvc.perform(get(BASE_URL + "/movies/" + movieId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.title").value("some-title-2"))
                .andExpect(jsonPath("$.data.description").value("some-description"))
                .andExpect(jsonPath("$.data.releaseDate").value("01-01-2020"));

        MovieDto updateMovieDto = MovieDto.builder()
                .title("t")
                .description("d")
                .releaseDate("r")
                .build();

        String json = objectMapper.writeValueAsString(updateMovieDto);

        // Update Check
        this.mockMvc.perform(patch(BASE_URL + "/movies/" + movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are not valid, see data for details"))
                .andExpect(jsonPath("$.data.title").value("title is required and length must be at least 3, alternatively please remove this field"))
                .andExpect(jsonPath("$.data.description").value("description is required and length must be at least 3, alternatively please remove this field"))
                .andExpect(jsonPath("$.data.releaseDate").value("releaseDate is required and length must be at least 3, alternatively please remove this field"));

        // After Update Check
        this.mockMvc.perform(get(BASE_URL + "/movies/" + movieId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.title").value("some-title-2"))
                .andExpect(jsonPath("$.data.description").value("some-description"))
                .andExpect(jsonPath("$.data.releaseDate").value("01-01-2020"));
    }

    @Test
    void testUpdateMovieThrowsWhenNotFoundMovieId() throws Exception {

        String movieId = "663fed2ac3bb554bca098c50";

        MovieDto updateMovieDto = MovieDto.builder()
                .title("updated-title")
                .description("updated-description")
                .releaseDate("01-01-2021")
                .build();

        String json = objectMapper.writeValueAsString(updateMovieDto);

        this.mockMvc.perform(patch(BASE_URL + "/movies/" + movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find movie with id " + movieId));

    }

    @Test
    void testUpdateMovieThrowsWhenInvalidMovieId() throws Exception {

        String movieId = "invalid123";

        MovieDto updateMovieDto = MovieDto.builder()
                .title("updated-title")
                .description("updated-description")
                .releaseDate("01-01-2021")
                .build();

        String json = objectMapper.writeValueAsString(updateMovieDto);

        this.mockMvc.perform(patch(BASE_URL + "/movies/" + movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("movie id: " + movieId + " is invalid, should be 24 characters long"));
    }

    @Test
    void testDeleteMovieByIdSuccess() throws Exception {
        String movieId = "663fed2ac3bb554bca098c59";

        mockMvc.perform(delete(BASE_URL + "/movies/" + movieId).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"));
    }

    @Test
    void testDeleteMovieByIdThrowsWhenMovieNotFound() throws Exception {
        String movieId = "663fed2ac3bb554bca098c50";

        mockMvc.perform(delete(BASE_URL + "/movies/" + movieId).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find movie with id " + movieId));
    }

    @Test
    void testDeleteMovieByIdThrowsWhenInvalidMovieId() throws Exception {
        String movieId = "invalid123";

        mockMvc.perform(delete(BASE_URL + "/movies/" + movieId).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("movie id: " + movieId + " is invalid, should be 24 characters long"));
    }

    @Test
    void testDeleteMovieByIdThrows401WhenNotAuthenticated() throws Exception {
        String movieId = "663fed2ac3bb554bca098c59";

        mockMvc.perform(delete(BASE_URL + "/movies/" + movieId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("Login credentials are missing."));
    }


    private void loginAndSetAuthToken() throws Exception {
        //login
        ResultActions resultActions = mockMvc.perform(post(BASE_URL + "/users/login")
                .with(httpBasic("john@test.com", "123456"))); // Note: Already Populated DB with this user --> See: StreamApiApplication::populateTestContainerDB

        // get and set token from login result
        JSONObject jsonObject = getJsonObjectFromResultActions(resultActions);
        this.token = "Bearer " + jsonObject.getJSONObject("data").getString("token");
    }

    public static @NotNull JSONObject getJsonObjectFromResultActions(ResultActions resultActions) throws Exception {
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(contentAsString);
        log.info("jsonObject: {}", jsonObject);
        return jsonObject;
    }

    public String getInvalidToken() {
        return "Bearer eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwiZXhwIjoxNzE1MzQxNTUwLCJpYXQiOjE3MTUzMzQzNTAsImF1dGhvcml0aWVzIjoiUk9MRV9hZG1pbiBST0xFX3VzZXIifQ.LSIsQ6a6xWp_dC7o27O-k6lfRQ15-5EG478ygmxbPW5l3Ej0Fg-U0_ofdApPQc8cT7AN9wdZedvGxLv8GpEhvz-osqV0NYqt9BC-AQoQdjXvGblkFiokBd1JY_jPjQ0VZTEim7ebidud8ByVYqgFAl5ApJUWgAzZljoaKgmJsKxgFKtFL32BFFvT6ffaqr7bnieIS9FHGC0oPKqGEXRja_NMZ4MDkrX28A7vyU-Kpb4JhtKXe970w2ZbcdbfpmaS3utSCIG1RdquRx5-Te6pkF53lVPEGd-QLLLOznOpDIMJ84Hzg2ylde7Ho-OSWkzsqHAHq_WrPdbZHkQMrkvFrA";
    }


}
