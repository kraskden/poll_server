package org.kraskovsky.polls.api;

import org.kraskovsky.polls.model.Poll;
import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.service.AnswerService;
import org.kraskovsky.polls.service.PollService;
import org.kraskovsky.polls.service.UserService;
import org.kraskovsky.polls.service.WsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vote/")
public class AnswerController {

    private final UserService userService;
    private final AnswerService answerService;
    private final PollService pollService;
    private final WsService wsService;

    @Autowired
    public AnswerController(@Lazy UserService userService, AnswerService answerService,
                            PollService pollService, WsService wsService) {
        this.userService = userService;
        this.answerService = answerService;
        this.pollService = pollService;
        this.wsService = wsService;
    }

    @GetMapping("/all")
    ResponseEntity<List<Map<String, String>>> getVotes() {
        Optional<User> optionalUser = userService.getUserFromSecurityContext();
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Optional<Poll> optionalPoll = pollService.getLastPoll(optionalUser.get());
        if (optionalPoll.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(answerService.getAnswers(optionalPoll.get()), HttpStatus.OK);
    }

    @GetMapping("/{pollId}/all")
    ResponseEntity<List<Map<String, String>>> getVotesByPoll(@PathVariable(value = "pollId") Long id) {
        Optional<User> optionalUser = userService.getUserFromSecurityContext();
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Optional<Poll> optionalPoll = pollService.getPollById(optionalUser.get(), id);
        if (optionalPoll.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(answerService.getAnswers(optionalPoll.get()), HttpStatus.OK);
    }

    @PostMapping("/add/{userId}")
    ResponseEntity<String> addVote(@PathVariable(value = "userId") Long id, @RequestBody Map<String, String> vote) {
        Optional<User> optionalOwner = userService.findById(id);
        if (id == null || vote == null) {
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }

        if (optionalOwner.isEmpty()) {
            return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
        }
        User owner = optionalOwner.get();

        Optional<Poll> optionalPoll = pollService.getLastPoll(owner);
        if (optionalPoll.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        Poll poll = optionalPoll.get();

        if (answerService.submit(poll, vote)) {
            wsService.sendAnswerToUser(owner, vote);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Bad request", HttpStatus.NOT_FOUND);
        }

    }

}
