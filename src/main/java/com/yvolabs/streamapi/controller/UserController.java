package com.yvolabs.streamapi.controller;

import com.yvolabs.streamapi.dto.UserDto;
import com.yvolabs.streamapi.model.StreamUser;
import com.yvolabs.streamapi.response.Result;
import com.yvolabs.streamapi.response.StatusCode;
import com.yvolabs.streamapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.yvolabs.streamapi.mapper.UserMapper.INSTANCE;

/**
 * @author Yvonne N
 */
@RestController
@RequestMapping("${api.endpoint.base-url}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Result<List<UserDto>>> findAllUsers() {
        List<StreamUser> users = userService.findAll();

        List<UserDto> usersDto = users.stream()
                .map(INSTANCE::userToUserDto)
                .toList();

        Result<List<UserDto>> result = Result.<List<UserDto>>builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Find All Success")
                .data(usersDto)
                .build();

        return ResponseEntity.ok(result);

    }

    @PostMapping
    public ResponseEntity<Result<UserDto>> addUser(@RequestBody @Valid StreamUser user) {
        StreamUser addedUser = userService.save(user);
        UserDto addedUserDto = INSTANCE.userToUserDto(addedUser);

        Result<UserDto> result = Result.<UserDto>builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Add User Success")
                .data(addedUserDto)
                .build();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Result<UserDto>> findUserById(@PathVariable String userId) {
        StreamUser user = userService.findById(userId);
        UserDto userDto = INSTANCE.userToUserDto(user);
        Result<UserDto> result = Result.<UserDto>builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Find User Success")
                .data(userDto)
                .build();

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Result<UserDto>> updateUser(@PathVariable String userId, @RequestBody @Valid UserDto userDto) {
        StreamUser updatedUser = userService.update(userId, userDto);
        UserDto updatedUserDto = INSTANCE.userToUserDto(updatedUser);

        Result<UserDto> result = Result.<UserDto>builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Update User Success")
                .data(updatedUserDto)
                .build();

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Result<?>> deleteUser(@PathVariable String userId) {
        userService.delete(userId);

        Result<?> result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Delete User Success")
                .data(null)
                .build();

        return ResponseEntity.ok(result);
    }

}
