package org.kraskovsky.polls.secure;

import lombok.extern.slf4j.Slf4j;
import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.secure.jwt.JwtUser;
import org.kraskovsky.polls.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class JwtUserDetailService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public JwtUserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> u = userService.findByEmail(s);
        if (u.isEmpty()) {
            throw new UsernameNotFoundException("User " + s + " not found");
        }

        log.info("IN loadUserByUsername - user with username: {} successfully loaded", s);

        return JwtUser.buildFromUser(u.get());
    }
}
