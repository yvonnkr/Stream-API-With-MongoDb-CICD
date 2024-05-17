package com.yvolabs.streamapi.security;

import com.yvolabs.streamapi.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author Yvonne N
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for Auth API endpoints")
@Slf4j
@Testcontainers
@ActiveProfiles("integration-test")
@Disabled
class AuthControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Value("${api.endpoint.base-url}")
    String BASE_URL;

    @ServiceConnection
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl("test-db"));
    }

    @Test
    void testGetLoginInfoSuccess() throws Exception {

        mockMvc.perform(post(BASE_URL + "/users/login")
                        .with(httpBasic("john@test.com", "123456"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("User Info and JSON Web Token"))
                .andExpect(jsonPath("$.data.token", Matchers.containsString("eyJh")))
                .andExpect(jsonPath("$.data.userInfo.firstName").value("john"))
                .andExpect(jsonPath("$.data.userInfo.lastName").value("doe"))
                .andExpect(jsonPath("$.data.userInfo.email").value("john@test.com"))
                .andExpect(jsonPath("$.data.userInfo.enabled").value("true"))
                .andExpect(jsonPath("$.data.userInfo.roles").value("admin user"));
    }

    @Test
    void testGetLoginInfoFailsWithIncorrectLoginCredentials() throws Exception {

        mockMvc.perform(post(BASE_URL + "/users/login")
                        .with(httpBasic("john@test.com", "wrongPassword"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("username or password is incorrect"));
    }

    @Test
    void testGetLoginInfoFailsWith401IfUserIsDisabled() throws Exception {

        // login with a DISABLED user
        mockMvc.perform(post(BASE_URL + "/users/login")
                        .with(httpBasic("sam@test.com", "123456"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("User account is abnormal"));

    }

    @Test
    void testGetLoginInfoFailsWithA403IfLoginUserRoleNotAdmin() throws Exception {

        // login with a user with no ADMIN role
        mockMvc.perform(post(BASE_URL + "/users/login")
                        .with(httpBasic("jane@test.com", "qwerty"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value("No permission to access this resource"));

    }

    @Test
    void testNoResourceWithUnknownEndpoint() throws Exception {

        mockMvc.perform(post(BASE_URL + "/unknownEndpoint")
                        .with(httpBasic("jane@test.com", "qwerty"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INTERNAL_SERVER_ERROR))
                .andExpect(jsonPath("$.message").value("No static resource api/v1/unknownEndpoint."));

    }

}