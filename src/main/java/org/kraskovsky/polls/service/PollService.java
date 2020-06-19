package org.kraskovsky.polls.service;

import org.kraskovsky.polls.model.Poll;
import org.kraskovsky.polls.model.User;

import java.util.Optional;

public interface PollService {
    Optional<Poll> getLastPoll(User user);
}
