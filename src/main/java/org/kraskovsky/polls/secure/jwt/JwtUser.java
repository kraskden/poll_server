package org.kraskovsky.polls.secure.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kraskovsky.polls.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class JwtUser implements UserDetails {

    @Getter
    private final Long id;

    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public static JwtUser buildFromUser(User u) {
        return new JwtUser(
                u.getId(),
                u.getEmail(),
                u.getPassword()
        );
    }

    public JwtUser(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = new ArrayList<GrantedAuthority>() {{
            add(new SimpleGrantedAuthority("USER"));
        }};
        log.warn("JwtUser builded");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return true;
    }
}
