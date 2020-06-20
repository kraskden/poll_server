package org.kraskovsky.polls.api;

import org.kraskovsky.polls.dto.poll.PollDto;
import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.service.PollService;
import org.kraskovsky.polls.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/poll")
public class PollController {

    private final PollService pollService;
    private final UserService userService;

    @Autowired
    public PollController(PollService pollService, UserService userService) {
        this.pollService = pollService;
        this.userService = userService;
    }

    @GetMapping("/all")
    ResponseEntity<List<PollDto>> getPolls() {
        Optional<User> user = getUser();
        if (user.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(pollService.getAllPolls(user.get()).stream()
                .map(PollDto::fromPoll)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    private Optional<User> getUser() {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails details = (UserDetails)obj;

        return userService.findByEmail(details.getUsername());
    }
}
