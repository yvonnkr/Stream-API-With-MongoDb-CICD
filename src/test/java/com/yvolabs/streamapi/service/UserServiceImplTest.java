package com.yvolabs.streamapi.service;

import com.yvolabs.streamapi.dto.UserDto;
import com.yvolabs.streamapi.exception.InvalidObjectIdException;
import com.yvolabs.streamapi.exception.ObjectNotFoundException;
import com.yvolabs.streamapi.exception.UserAlreadyExistsException;
import com.yvolabs.streamapi.model.StreamUser;
import com.yvolabs.streamapi.repository.UserRepository;
import com.yvolabs.streamapi.utils.UserTestData;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * @author Yvonne N
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private List<StreamUser> userList;

    @BeforeEach
    void setUp() {
        userList = UserTestData.setUsersTestData();

    }

    @Test
    void testFindAllSuccess() {
        given(userRepository.findAll()).willReturn(userList);
        List<StreamUser> users = userService.findAll();

        Assertions.assertThat(users).hasSize(userList.size());
        verify(userRepository).findAll();
    }

    @Test
    void testSaveSuccess() {
        StreamUser newUser = StreamUser.builder()
                .id(new ObjectId("66367b04d98bbb6418dbda61"))
                .firstName("john")
                .lastName("doe")
                .email("john@doe.com")
                .password("123456")
                .enabled(true)
                .roles("admin user")
                .build();

        given(userRepository.save(newUser)).willReturn(newUser);
        given(passwordEncoder.encode(newUser.getPassword())).willReturn("encoded-password");
        StreamUser savedUser = userService.save(newUser);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isEqualTo(newUser.getId());
        Assertions.assertThat(savedUser.getFirstName()).isEqualTo(newUser.getFirstName());
        Assertions.assertThat(savedUser.getLastName()).isEqualTo(newUser.getLastName());
        Assertions.assertThat(savedUser.getEmail()).isEqualTo(newUser.getEmail());
        Assertions.assertThat(savedUser.getPassword()).isEqualTo(newUser.getPassword());
        Assertions.assertThat(savedUser.isEnabled()).isEqualTo(newUser.isEnabled());
        Assertions.assertThat(savedUser.getRoles()).isEqualTo(newUser.getRoles());
        verify(userRepository).save(newUser);

    }


    @Test
    void testSaveWillThrowWhenUserEmailAlreadyExists() {
        String email = "john@doe.com";
        StreamUser newUser = StreamUser.builder()
                .firstName("john")
                .lastName("doe")
                .email(email)
                .password("123456")
                .enabled(true)
                .roles("admin user")
                .build();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(userList.get(0)));
        given(passwordEncoder.encode(newUser.getPassword())).willReturn("encoded-password");

        Throwable throwable = catchThrowable(() -> userService.save(newUser));

        assertThat(throwable)
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User with email " + email + " already exists");
        verify(userRepository).findByEmail(email);
    }


    @Test
    void tesFindByIdSuccess() {
        String userId = "66367b04d98bbb6418dbda61";
        StreamUser foundUser = userList.get(0);
        given(userRepository.findById(new ObjectId(userId))).willReturn(Optional.of(foundUser));
        StreamUser user = userService.findById(userId);

        Assertions.assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(foundUser.getId());
        assertThat(user.getFirstName()).isEqualTo(foundUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(foundUser.getLastName());
        assertThat(user.getEmail()).isEqualTo(foundUser.getEmail());
        assertThat(user.getPassword()).isEqualTo(foundUser.getPassword());
        assertThat(user.isEnabled()).isEqualTo(foundUser.isEnabled());
        assertThat(user.getRoles()).isEqualTo(foundUser.getRoles());
        verify(userRepository).findById(new ObjectId(userId));

    }


    @Test
    void tesFindByIdThrowsWhenUserNotFound() {
        String userId = "66367b04d98bbb6418dbda61";
        doThrow(new ObjectNotFoundException("user", userId)).when(userRepository).findById(new ObjectId(userId));
        Throwable throwable = catchThrowable(() -> userService.findById(userId));

        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Could not find user with id " + userId);
        verify(userRepository).findById(new ObjectId(userId));
    }

    @Test
    void testUpdateSuccess() {
        String userId = "66367b04d98bbb6418dbda61";
        StreamUser foundUser = userList.get(0);
        UserDto userDto = UserDto.builder()
                .firstName("john_updated")
                .lastName("doe_updated")
                .roles("admin")
                .build();

        StreamUser updatedUser = StreamUser.builder()
                .id(new ObjectId(userId))
                .firstName("john_updated")
                .lastName("doe_updated")
                .email("john@doe.com")
                .password("123456")
                .enabled(true)
                .roles("admin")
                .build();

        given(userRepository.findById(new ObjectId(userId))).willReturn(Optional.of(foundUser));
        given(userRepository.save(foundUser)).willReturn(updatedUser);
        StreamUser user = userService.update(userId, userDto);

        Assertions.assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(updatedUser.getId());
        assertThat(user.getFirstName()).isEqualTo(updatedUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(updatedUser.getLastName());
        assertThat(user.getEmail()).isEqualTo(updatedUser.getEmail());
        assertThat(user.getPassword()).isEqualTo(updatedUser.getPassword());
        assertThat(user.isEnabled()).isEqualTo(updatedUser.isEnabled());
        assertThat(user.getRoles()).isEqualTo(updatedUser.getRoles());
        verify(userRepository).findById(new ObjectId(userId));
        verify(userRepository).save(foundUser);

    }

    @Test
    void testUpdateThrowsWhenUserNotFound() {
        String userId = "66367b04d98bbb6418dbda61";
        doThrow(new ObjectNotFoundException("user", userId)).when(userRepository).findById(new ObjectId(userId));

        Throwable throwable = catchThrowable(() -> userService.update(userId, UserDto.builder().build()));
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Could not find user with id " + userId);
        verify(userRepository).findById(new ObjectId(userId));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void testDeleteSuccess() {
        String userId = "66367b04d98bbb6418dbda61";
        given(userRepository.findById(new ObjectId(userId))).willReturn(Optional.of(userList.get(0)));
        doNothing().when(userRepository).deleteById(new ObjectId(userId));
        userService.delete(userId);
        verify(userRepository).deleteById(new ObjectId(userId));
    }

    @Test
    void testDeleteThrowsWhenUserNotFound() {
        String userId = "66367b04d98bbb6418dbda61";
        doThrow(new ObjectNotFoundException("user", userId)).when(userRepository).findById(new ObjectId(userId));
        Throwable throwable = catchThrowable(() -> userService.delete(userId));
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Could not find user with id " + userId);
        verify(userRepository).findById(new ObjectId(userId));
        verify(userRepository, times(0)).deleteById(new ObjectId(userId));
    }

    @Test
    void testDeleteThrowsWhenUserIdIsInvalid() {
        String userId = "invalidId";
      Throwable throwable = catchThrowable(() -> userService.delete(userId));
      assertThat(throwable)
      .isInstanceOf(InvalidObjectIdException.class);

    }

}