package com.yvolabs.streamapi.utils;

import com.yvolabs.streamapi.model.StreamUser;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yvonne N
 */
public class UserTestData {

    public static List<StreamUser> setUsersTestData() {

        StreamUser user1 = StreamUser.builder()
                .id(new ObjectId("66367b04d98bbb6418dbda61"))
                .firstName("john")
                .lastName("doe")
                .email("john@doe.com")
                .password("123456")
                .enabled(true)
                .roles("admin user")
                .build();

        StreamUser user2 = StreamUser.builder()
                .id(new ObjectId("66367b04d98bbb6418dbda62"))
                .firstName("jane")
                .lastName("doe")
                .email("jane@doe.com")
                .password("qwerty")
                .enabled(true)
                .roles("user")
                .build();

        List<StreamUser> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        return userList;
    }
}
