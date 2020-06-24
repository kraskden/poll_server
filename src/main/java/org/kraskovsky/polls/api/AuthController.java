package org.kraskovsky.polls.api;

import lombok.extern.slf4j.Slf4j;
import org.kraskovsky.polls.dto.login.LoginReqDto;
import org.kraskovsky.polls.dto.login.LoginResDto;
import org.kraskovsky.polls.dto.recover.PasswordRecoverDto;
import org.kraskovsky.polls.dto.register.RegisterReqDto;
import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.secure.jwt.JwtTokenProvider;
import org.kraskovsky.polls.service.EmailService;
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
    private final EmailService emailService;

    @Autowired
    public AuthController(AuthenticationManager authManager, JwtTokenProvider jwtProvider, UserService userService, EmailService emailService) {
        this.authManager = authManager;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
        this.emailService = emailService;
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
        emailService.sendSimpleMessage(user.getEmail(), "Welcome to the party!",
                String.format("Login: %s \n Password: %s\n", reqDto.getEmail(), reqDto.getPassword()));

        return ResponseEntity.ok().body("User register successfully");
    }

    @PostMapping("resetPassword")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordRecoverDto recoverDto) {
        Optional<User> optionalUser =  userService.findByEmail(recoverDto.getEmail());
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>("No such user", HttpStatus.BAD_REQUEST);
        }
        String newPassword = userService.resetPassword(optionalUser.get());
        emailService.sendSimpleMessage(recoverDto.getEmail(), "Reset Password",
                String.format("New password:\n%s", newPassword ));
        return new ResponseEntity<>("Password has been reseted and sended to email", HttpStatus.OK);
    }

}
