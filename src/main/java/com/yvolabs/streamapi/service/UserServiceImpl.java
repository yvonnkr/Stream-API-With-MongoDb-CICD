package com.yvolabs.streamapi.service;

import com.yvolabs.streamapi.dto.UserDto;
import com.yvolabs.streamapi.exception.InvalidObjectIdException;
import com.yvolabs.streamapi.exception.ObjectNotFoundException;
import com.yvolabs.streamapi.exception.UserAlreadyExistsException;
import com.yvolabs.streamapi.model.StreamUser;
import com.yvolabs.streamapi.repository.UserRepository;
import com.yvolabs.streamapi.security.MyUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.yvolabs.streamapi.mapper.UserMapper.INSTANCE;

/**
 * @author Yvonne N
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<StreamUser> findAll() {
        return userRepository.findAll();
    }

    @Override
    public StreamUser save(StreamUser user) {
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        userRepository.findByEmail(user.getEmail())
                .ifPresent((foundUser) -> {
                    throw new UserAlreadyExistsException(foundUser.getEmail());
                });

        return userRepository.save(user);
    }

    @Override
    public StreamUser findById(String userId) {
        ObjectId convertedUserId = convertStringToObjectId(userId);

        return userRepository.findById(convertedUserId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));

    }

    @Override
    public StreamUser update(String userId, UserDto userDto) {
        ObjectId convertedUserId = convertStringToObjectId(userId);

        return userRepository.findById(convertedUserId)
                .map((foundUser) -> {
                    StreamUser userUpdate = INSTANCE.updateUserDto(userDto, foundUser);
                    return userRepository.save(userUpdate);
                })
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
    }

    @Override
    public void delete(String userId) {
        userRepository.findById(convertStringToObjectId(userId))
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        userRepository.deleteById(convertStringToObjectId(userId));

    }

    //security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(MyUserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("user with email " + username + " was not found"));
    }


    private static ObjectId convertStringToObjectId(String userId) {
        ObjectId userIdToObjectId;

        try {
            userIdToObjectId = new ObjectId(userId);
        } catch (IllegalArgumentException e) {
            throw new InvalidObjectIdException("user", userId);
        }

        return userIdToObjectId;
    }


}
