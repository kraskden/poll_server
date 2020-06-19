package org.kraskovsky.polls.api;

import lombok.extern.slf4j.Slf4j;
import org.kraskovsky.polls.dto.login.LoginReqDto;
import org.kraskovsky.polls.dto.login.LoginResDto;
import org.kraskovsky.polls.dto.register.RegisterReqDto;
import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.secure.jwt.JwtTokenProvider;
import org.kraskovsky.polls.secure.jwt.exception.JwtAuthException;
import org.kraskovsky.polls.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/auth/")
@Slf4j
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtProvider;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authManager, JwtTokenProvider jwtProvider, UserService userService) {
        this.authManager = authManager;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity<LoginResDto> login(@Valid @RequestBody LoginReqDto reqDto) {
        try {
            String email = reqDto.getEmail();
            log.info("Login {}", reqDto);
            authManager.authenticate(new UsernamePasswordAuthenticationToken(email, reqDto.getPassword()));
            Optional<User> user = userService.findByEmail(email);
            if (user.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }

            String token = jwtProvider.createToken(email);
            return ResponseEntity.ok(new LoginResDto(email, token));

        } catch (AuthenticationException e) {
            log.warn("BAD PASSWORD");
            throw new BadCredentialsException("Invalid password");
        }
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterReqDto reqDto) {
        String email = reqDto.getEmail();

        if (userService.findByEmail(email).isPresent()) {
            return new ResponseEntity<String>("User already exists", HttpStatus.BAD_REQUEST);
        }

        User user = new User(
                email, reqDto.getPassword(), reqDto.getFirstName(), reqDto.getLastName(), reqDto.getPhone());

        userService.register(user);

        return ResponseEntity.ok().body("User register successfully");
    }

}
