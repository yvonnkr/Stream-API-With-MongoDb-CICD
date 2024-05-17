package com.yvolabs.streamapi.security;

import com.yvolabs.streamapi.dto.UserDto;
import com.yvolabs.streamapi.mapper.UserMapper;
import com.yvolabs.streamapi.model.StreamUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yvonne N
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        // Create User
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        StreamUser streamUser = principal.getStreamUser();
        UserDto userDto = UserMapper.INSTANCE.userToUserDto(streamUser);

        //Create Token
        String token = jwtProvider.createToken(authentication);

        // Create Map
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("userInfo", userDto);
        loginInfo.put("token", token);

        return loginInfo;
    }
}
