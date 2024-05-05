package com.yvolabs.streamapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yvolabs.streamapi.dto.UserDto;
import com.yvolabs.streamapi.exception.InvalidObjectIdException;
import com.yvolabs.streamapi.exception.ObjectNotFoundException;
import com.yvolabs.streamapi.exception.UserAlreadyExistsException;
import com.yvolabs.streamapi.model.StreamUser;
import com.yvolabs.streamapi.response.StatusCode;
import com.yvolabs.streamapi.service.UserService;
import com.yvolabs.streamapi.utils.UserTestData;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author Yvonne N
 */
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    List<StreamUser> usersList;

    @Value("${api.endpoint.base-url}/users")
    String PATH;

    @BeforeEach
    void setUp() {
        usersList = UserTestData.setUsersTestData();
    }

    @Test
    void testFindAllUsers() throws Exception {
        given(userService.findAll()).willReturn(usersList);

        mockMvc.perform(get(PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value("true"))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(usersList.size())));
        verify(userService).findAll();
    }

    @Test
    void testAddUserSuccess() throws Exception {
        StreamUser newUserRequest = StreamUser.builder()
                .firstName("john")
                .lastName("doe")
                .email("john@doe.com")
                .password("123456")
                .enabled(true)
                .roles("admin,user")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(newUserRequest);

        StreamUser createdUser = StreamUser.builder()
                .id(new ObjectId("66367b04d98bbb6418dbda61"))
                .firstName("john")
                .lastName("doe")
                .email("john@doe.com")
                .password("123456")
                .enabled(true)
                .roles("admin,user")
                .build();

        given(userService.save(newUserRequest)).willReturn(createdUser);

        mockMvc.perform(post(PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value("true"))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add User Success"))
                .andExpect(jsonPath("$.data.id").value(createdUser.getId().toString()))
                .andExpect(jsonPath("$.data.firstName").value(createdUser.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(createdUser.getLastName()))
                .andExpect(jsonPath("$.data.email").value(createdUser.getEmail()))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.enabled").value(createdUser.isEnabled()))
                .andExpect(jsonPath("$.data.roles").value(createdUser.getRoles()));
        verify(userService).save(newUserRequest);

    }

    @Test
    void testAddUserThrowsWhenInvalidRequestData() throws Exception {
        StreamUser newUserRequest = StreamUser.builder()
                .firstName("")
                .lastName("")
                .email("")
                .password("123")
                .enabled(false)
                .roles("")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(newUserRequest);

        mockMvc.perform(post(PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect((result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException())))
                .andExpect(jsonPath("$.flag").value("false"))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are not valid, see data for details"))
                .andExpect(jsonPath("$.data.firstName").value("firstName is required"))
                .andExpect(jsonPath("$.data.lastName").value("lastName is required"))
                .andExpect(jsonPath("$.data.email").value("email is required"))
                .andExpect(jsonPath("$.data.password").value("password should be at least 6 characters long"))
                .andExpect(jsonPath("$.data.roles").value("roles are required"));
        verify(userService, times(0)).save(any());
    }

    @Test
    void testAddUserThrowsWhenEmailAlreadyExists() throws Exception {
        StreamUser newUserRequest = StreamUser.builder()
                .firstName("john")
                .lastName("doe")
                .email("john@doe.com")
                .password("123456")
                .enabled(true)
                .roles("admin,user")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(newUserRequest);

        doThrow(new UserAlreadyExistsException(newUserRequest.getEmail())).when(userService).save(any());

        mockMvc.perform(post(PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value("false"))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("User with email " + newUserRequest.getEmail() + " already exists"));
        verify(userService).save(any());

    }

    @Test
    void testFindUserByIdSuccess() throws Exception {
        String userId = "66367b04d98bbb6418dbda61";
        StreamUser foundUser = usersList.get(0);
        given(userService.findById(userId)).willReturn(foundUser);

        mockMvc.perform(get(PATH + "/" + userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value("true"))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find User Success"))
                .andExpect(jsonPath("$.data.id").value(foundUser.getId().toString()))
                .andExpect(jsonPath("$.data.firstName").value(foundUser.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(foundUser.getLastName()))
                .andExpect(jsonPath("$.data.email").value(foundUser.getEmail()))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.enabled").value(foundUser.isEnabled()))
                .andExpect(jsonPath("$.data.roles").value(foundUser.getRoles()));
        verify(userService).findById(userId);
    }

    @Test
    void testFindUserByIdThrowsWhenRequestUserIdIsInvalid() throws Exception {
        String userId = "invalidUserId";
        doThrow(new InvalidObjectIdException("user", userId)).when(userService).findById(userId);

        mockMvc.perform(get(PATH + "/" + userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(InvalidObjectIdException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value("false"))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("user id: " + userId + " is invalid, should be 24 characters long"));
        verify(userService).findById(userId);

    }


    @Test
    void testFindUserByIdThrowsWhenUserDoesNotExist() throws Exception {
        String userId = "66367b04d98bbb6418dbda61";
        doThrow(new ObjectNotFoundException("user", userId)).when(userService).findById(userId);

        mockMvc.perform(get(PATH + "/" + userId).accept(MediaType.APPLICATION_JSON))
                .andExpect((result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException())))
                .andExpect(jsonPath("$.flag").value("false"))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with id " + userId));
        verify(userService).findById(userId);
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        String userId = "66367b04d98bbb6418dbda61";
        UserDto userDto = UserDto.builder()
                .firstName("john_updated")
                .lastName("doe_updated")
                .email("john_updated@doe.com")
                .enabled(false)
                .roles("user")
                .build();

        StreamUser updatedUser = StreamUser.builder()
                .id(new ObjectId(userId))
                .firstName("john_updated")
                .lastName("doe_updated")
                .email("john_updated@doe.com")
                .password("123456")
                .enabled(false)
                .roles("user")
                .build();

        String requestJson = objectMapper.writeValueAsString(userDto);

        given(userService.update(eq(userId), any())).willReturn(updatedUser);

        mockMvc.perform(put(PATH + "/" + userId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value("true"))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update User Success"))
                .andExpect(jsonPath("$.data.id").value(updatedUser.getId().toString()))
                .andExpect(jsonPath("$.data.firstName").value(updatedUser.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(updatedUser.getLastName()))
                .andExpect(jsonPath("$.data.email").value(updatedUser.getEmail()))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.enabled").value(updatedUser.isEnabled()))
                .andExpect(jsonPath("$.data.roles").value(updatedUser.getRoles()));
        verify(userService).update(eq(userId), any());
    }

    @Test
    void testUpdateUserThrowsWhenInvalidRequestArguments() throws Exception {
        String userId = "66367b04d98bbb6418dbda61";
        UserDto userDto = UserDto.builder()
                .firstName("")
                .lastName("")
                .roles("")
                .build();

        String requestJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(put(PATH + "/" + userId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value("false"))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are not valid, see data for details"))
                .andExpect(jsonPath("$.data.firstName").value("firstName is required"))
                .andExpect(jsonPath("$.data.lastName").value("lastName is required"))
                .andExpect(jsonPath("$.data.roles").value("roles are required"));
        verify(userService, times(0)).update(eq(userId), any());
    }

    @Test
    void testUpdateUserThrowsWhenRequestUserIdIsInvalid() throws Exception {
        String userId = "66367b04d98bbb6418dbda61";
        UserDto userDto = UserDto.builder()
                .firstName("john_updated")
                .lastName("doe_updated")
                .roles("user")
                .build();

        String requestJson = objectMapper.writeValueAsString(userDto);

        doThrow(new InvalidObjectIdException("user", userId)).when(userService).update(eq(userId), any());

        mockMvc.perform(put(PATH + "/" + userId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(InvalidObjectIdException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value("false"))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("user id: " + userId + " is invalid, should be 24 characters long"));
        verify(userService).update(eq(userId), any());
    }

    @Test
    void testUpdateUserThrowsWhenUserDoesNotExist() throws Exception {
        String userId = "66367b04d98bbb6418dbda61";
        UserDto userDto = UserDto.builder()
                .firstName("john_updated")
                .lastName("doe_updated")
                .roles("user")
                .build();

        String requestJson = objectMapper.writeValueAsString(userDto);

        doThrow(new ObjectNotFoundException("user", userId)).when(userService).update(eq(userId), any());

        mockMvc.perform(put(PATH + "/" + userId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value("false"))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with id " + userId));
        verify(userService).update(eq(userId), any());

    }

    @Test
    void deleteUserSuccess() throws Exception {
        String userId = "66367b04d98bbb6418dbda61";

        doNothing().when(userService).delete(eq(userId));
        mockMvc.perform(delete(PATH + "/" + userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value("true"))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete User Success"))
                .andExpect(jsonPath("$.data").doesNotExist());
        verify(userService).delete(eq(userId));
    }

    @Test
    void deleteUserThrowsWhenRequestUserIdIsInvalid() throws Exception {
        String userId = "66367b04d98bbb6418dbda61";
        doThrow(new InvalidObjectIdException("user", userId)).when(userService).delete(eq(userId));

        mockMvc.perform(delete(PATH + "/" + userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(InvalidObjectIdException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value("false"))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("user id: " + userId + " is invalid, should be 24 characters long"));
        verify(userService).delete(eq(userId));
    }

    @Test
    void deleteUserThrowsWhenUserDoesNotExist() throws Exception {
        String userId = "66367b04d98bbb6418dbda61";
        doThrow(new ObjectNotFoundException("user", userId)).when(userService).delete(eq(userId));

        mockMvc.perform(delete(PATH + "/" + userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value("false"))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with id " + userId));
        verify(userService).delete(eq(userId));
    }


}