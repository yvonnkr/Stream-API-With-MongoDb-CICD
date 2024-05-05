package com.yvolabs.streamapi.service;

import com.yvolabs.streamapi.dto.UserDto;
import com.yvolabs.streamapi.model.StreamUser;

import java.util.List;

/**
 * @author Yvonne N
 */
public interface UserService {
    List<StreamUser> findAll();

    StreamUser save(StreamUser streamUserRequest);

    StreamUser findById(String userId);


    StreamUser update(String userId, UserDto userDto);

    void delete(String userId);

}
