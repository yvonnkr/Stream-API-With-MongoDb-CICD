package com.yvolabs.streamapi.security;

import com.yvolabs.streamapi.model.StreamUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Yvonne N
 */
@Getter
@AllArgsConstructor
@Slf4j
public class MyUserPrincipal implements UserDetails {

    private StreamUser streamUser;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String[] roles = StringUtils.tokenizeToStringArray(this.streamUser.getRoles(), " ");
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = Arrays.stream(roles)
                .map((role) -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        log.info("Authorities: {}", simpleGrantedAuthorities);

        return simpleGrantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.streamUser.getPassword();
    }

    @Override
    public String getUsername() {
        return this.streamUser.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.streamUser.isEnabled();
    }
}
