package org.kraskovsky.polls.api;

import org.kraskovsky.polls.dto.profile.PasswordDto;
import org.kraskovsky.polls.dto.profile.ProfileDto;
import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.service.EmailService;
import org.kraskovsky.polls.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public ProfileController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/update")
    ResponseEntity<String> updateProfile(@RequestBody @Valid ProfileDto profileDto) {
        Optional<User> optionalUser =  userService.getUserFromSecurityContext();
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        User user = optionalUser.get();

        userService.updateProfile(user, profileDto.toUser());
        return new ResponseEntity<>("Updated", HttpStatus.OK);
    }

    @PostMapping("/passChange")
    ResponseEntity<String> changePassword(@RequestBody @Valid PasswordDto passwordDto) {
        Optional<User> optionalUser =  userService.getUserFromSecurityContext();
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        User user = optionalUser.get();

        String email = user.getEmail();

        if (userService.changePassword(user, passwordDto.getCurrent(), passwordDto.getUpdated())) {

            emailService.sendSimpleMessage(email, "Password changed",
                    String.format("New password is '%s'", passwordDto.getUpdated()));
            return new ResponseEntity<>("Changed", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid password", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/")
    ResponseEntity<ProfileDto> getProfileInfo() {
        Optional<User> optionalUser =  userService.getUserFromSecurityContext();
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        User user = optionalUser.get();

        return new ResponseEntity<>(ProfileDto.fromUser(optionalUser.get()), HttpStatus.OK);
    }


}
