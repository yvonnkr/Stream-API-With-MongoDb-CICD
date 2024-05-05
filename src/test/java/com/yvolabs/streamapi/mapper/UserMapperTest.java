package com.yvolabs.streamapi.mapper;

import com.yvolabs.streamapi.dto.UserDto;
import com.yvolabs.streamapi.model.StreamUser;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.yvolabs.streamapi.mapper.UserMapper.INSTANCE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Yvonne N
 */
class UserMapperTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void testUserToUserDto() {
        StreamUser user = StreamUser.builder()
                .id(new ObjectId("66367b04d98bbb6418dbda61"))
                .firstName("john")
                .lastName("doe")
                .email("john@doe.com")
                .password("123456")
                .enabled(true)
                .roles("admin,user")
                .build();

        UserDto userDto = INSTANCE.userToUserDto(user);
        assertNotNull(userDto);
        assertEquals(userDto.getId(), user.getId().toString());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getRoles(), user.getRoles());
    }

    @Test
    void testUserDtoToStreamUser() {
        UserDto userDto = UserDto.builder()
                .id("66367b04d98bbb6418dbda61")
                .firstName("john")
                .lastName("doe")
                .email("john@doe.com")
                .enabled(true)
                .roles("admin,user")
                .build();

        StreamUser user = INSTANCE.userDtoToStreamUser(userDto);

        assertNotNull(user);
        assertEquals(user.getId(), new ObjectId(userDto.getId()));
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getRoles(), userDto.getRoles());

    }

    @Test
    void testUpdateUserDto() {
        UserDto userDto = UserDto.builder()
                .id("66367b04d98bbb6418dbda61")
                .firstName("john")
                .lastName("doe")
                .email("john@doe.com")
                .enabled(true)
                .roles("admin,user")
                .build();

        StreamUser updatedUser = StreamUser.builder()
                .id(new ObjectId("66367b04d98bbb6418dbda61"))
                .firstName("john")
                .lastName("doe")
                .email("john@doe.com")
                .password("123456")
                .enabled(true)
                .roles("admin,user")
                .build();

        INSTANCE.updateUserDto(userDto, updatedUser);
        assertEquals(updatedUser.getId(), new ObjectId(userDto.getId()));
        assertEquals(updatedUser.getFirstName(), userDto.getFirstName());
        assertEquals(updatedUser.getLastName(), userDto.getLastName());
        assertEquals(updatedUser.getEmail(), userDto.getEmail());
        assertEquals(updatedUser.getRoles(), userDto.getRoles());
    }

    @Test
    void testObjectIdToString() {
        String str = UserMapper.objectIdToString(new ObjectId("662329256487b26751b3d406"));
        assertNotNull(str);
        assertEquals(str, "662329256487b26751b3d406");
    }


    @Test
    void testStringToObjectId() {
        ObjectId objectId = UserMapper.stringToObjectId("662329256487b26751b3d406");
        assertNotNull(objectId);
        assertEquals(objectId, new ObjectId("662329256487b26751b3d406"));

        assertNull(UserMapper.stringToObjectId(""));
        assertNull(UserMapper.stringToObjectId(null));
    }
}