package com.yvolabs.streamapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yvolabs.streamapi.dto.UserDto;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author Yvonne N
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for User API endpoints")
@Slf4j
@Testcontainers
@ActiveProfiles("integration-test")
@Disabled
public class UserControllerIntegrationTest {
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
    void setup() throws Exception {
        loginAndSetAuthToken();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindAllUsersSuccess() throws Exception {
        mockMvc.perform(get(BASE_URL + "/users").accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)));
    }

    @Test
    void testFindAllUsersThrowsWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get(BASE_URL + "/users").accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("Login credentials are missing."));
    }

    @Test
    void testFindUserByIdSuccess() throws Exception {
        String userId = "663fed2ac3bb554bca098c58";

        mockMvc.perform(get(BASE_URL + "/users/" + userId).accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find User Success"))
                .andExpect(jsonPath("$.data.firstName").value("john"))
                .andExpect(jsonPath("$.data.lastName").value("doe"))
                .andExpect(jsonPath("$.data.email").value("john@test.com"))
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.roles").value("admin user"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void testFindUserByIdThrowsIfUserIdNotFound() throws Exception {
        String nonExistentUserId = "663fed2ac3bb554bca098c50";

        mockMvc.perform(get(BASE_URL + "/users/" + nonExistentUserId).accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with id " + nonExistentUserId));
    }

    @Test
    void testFindUserByIdThrowsIfInvalidUserIdInRequest() throws Exception {
        String invalidUserId = "invalidId123";

        mockMvc.perform(get(BASE_URL + "/users/" + invalidUserId).accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("user id: " + invalidUserId + " is invalid, should be 24 characters long"));
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        String userId = "6641181ad9650d562fa633ab";

        // Before Update Check
        mockMvc.perform(get(BASE_URL + "/users/" + userId).accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find User Success"))
                .andExpect(jsonPath("$.data.firstName").value("jane"))
                .andExpect(jsonPath("$.data.lastName").value("doe"))
                .andExpect(jsonPath("$.data.email").value("jane@test.com"))
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.roles").value("user"))
                .andExpect(jsonPath("$.data.password").doesNotExist());

        UserDto userDtoUpdate = UserDto.builder()
                .firstName("jane_updated")
                .lastName("doe_updated")
                .enabled(false)
                .roles("user")
                .build();

        String json = objectMapper.writeValueAsString(userDtoUpdate);

        // Update Check
        mockMvc.perform(put(BASE_URL + "/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update User Success"))
                .andExpect(jsonPath("$.data.firstName").value("jane_updated"))
                .andExpect(jsonPath("$.data.lastName").value("doe_updated"))
                .andExpect(jsonPath("$.data.email").value("jane@test.com"))
                .andExpect(jsonPath("$.data.enabled").value(false))
                .andExpect(jsonPath("$.data.password").doesNotExist());


        // After Update Check
        mockMvc.perform(get(BASE_URL + "/users/" + userId).accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find User Success"))
                .andExpect(jsonPath("$.data.firstName").value("jane_updated"))
                .andExpect(jsonPath("$.data.lastName").value("doe_updated"))
                .andExpect(jsonPath("$.data.email").value("jane@test.com"))
                .andExpect(jsonPath("$.data.enabled").value(false))
                .andExpect(jsonPath("$.data.roles").value("user"))
                .andExpect(jsonPath("$.data.password").doesNotExist());

    }

    @Test
    void testUpdateUserThrowsValidationErrorOnInvalidData() throws Exception {
        String userId = "6641181ad9650d562fa633ab";

        // Before Update Check
        mockMvc.perform(get(BASE_URL + "/users/" + userId).accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find User Success"))
                .andExpect(jsonPath("$.data.firstName").value("jane"))
                .andExpect(jsonPath("$.data.lastName").value("doe"))
                .andExpect(jsonPath("$.data.email").value("jane@test.com"))
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.roles").value("user"))
                .andExpect(jsonPath("$.data.password").doesNotExist());

        UserDto userDtoUpdate = UserDto.builder()
                .firstName("")
                .lastName("")
                .enabled(false)
                .roles("")
                .build();

        String json = objectMapper.writeValueAsString(userDtoUpdate);

        // Update Check
        mockMvc.perform(put(BASE_URL + "/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are not valid, see data for details"))
                .andExpect(jsonPath("$.data.firstName").value("firstName is required"))
                .andExpect(jsonPath("$.data.lastName").value("lastName is required"))
                .andExpect(jsonPath("$.data.roles").value("roles are required"));


        // After Update Check - Existing User Was NOT Updated
        mockMvc.perform(get(BASE_URL + "/users/" + userId).accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find User Success"))
                .andExpect(jsonPath("$.data.firstName").value("jane"))
                .andExpect(jsonPath("$.data.lastName").value("doe"))
                .andExpect(jsonPath("$.data.email").value("jane@test.com"))
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.roles").value("user"))
                .andExpect(jsonPath("$.data.password").doesNotExist());

    }

    @Test
    void testUpdateUserThrowsUserNotFoundErrorIfIdNotFound() throws Exception {
        String userId = "6641181ad9650d562fa633a0";

        UserDto userDtoUpdate = UserDto.builder()
                .firstName("jane_update")
                .lastName("doe_update")
                .enabled(false)
                .roles("user")
                .build();

        String json = objectMapper.writeValueAsString(userDtoUpdate);

        // Update Check
        mockMvc.perform(put(BASE_URL + "/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with id " + userId));
    }

    @Test
    void testUpdateUserThrowsWhenInvalidUserId() throws Exception {
        String userId = "invalid123";

        UserDto userDtoUpdate = UserDto.builder()
                .firstName("jane_update")
                .lastName("doe_update")
                .enabled(false)
                .roles("user")
                .build();

        String json = objectMapper.writeValueAsString(userDtoUpdate);

        // Update Check
        mockMvc.perform(put(BASE_URL + "/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("user id: " + userId + " is invalid, should be 24 characters long"));
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        String userId = "6641181ad9650d562fa633ab";

        mockMvc.perform(delete(BASE_URL + "/users/" + userId).accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete User Success"));
    }

    @Test
    void testDeleteUserThrowsIfUserNotFound() throws Exception {
        String userId = "6641181ad9650d562fa633a0";

        mockMvc.perform(delete(BASE_URL + "/users/" + userId).accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with id " + userId));
    }

    @Test
    void testDeleteThrowsIfInvalidUserId() throws Exception {
        String userId = "invalid123";

        mockMvc.perform(delete(BASE_URL + "/users/" + userId).accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("user id: " + userId + " is invalid, should be 24 characters long"));
    }


    private void loginAndSetAuthToken() throws Exception {
        // login
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


}
