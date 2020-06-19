package org.kraskovsky.polls.api;

import lombok.extern.slf4j.Slf4j;
import org.kraskovsky.polls.model.Field;
import org.kraskovsky.polls.model.FieldType;
import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.service.FieldService;
import org.kraskovsky.polls.service.PollService;
import org.kraskovsky.polls.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api/user/test")
@Slf4j
public class UserProfileController {

    private final  UserService userService;
    private final PollService pollService;
    private final FieldService fieldService;

    @Autowired
    public UserProfileController(UserService userService, PollService pollService, FieldService fieldService) {
        this.userService = userService;
        this.pollService = pollService;
        this.fieldService = fieldService;
    }

    @GetMapping("hello")
    public String userAccess() {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails details = (UserDetails)obj;

        Optional<User> optionalUser = userService.findByEmail(details.getUsername());
        optionalUser.ifPresent(user -> {
            fieldService.addField(user, new Field("hello", true, true, FieldType.SINGLE_TEXT));
        });

        return details.getUsername();
    }
}
