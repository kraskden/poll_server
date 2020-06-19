package org.kraskovsky.polls.service.impl;

import org.kraskovsky.polls.model.Poll;
import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.repository.PollRepository;
import org.kraskovsky.polls.repository.UserRepository;
import org.kraskovsky.polls.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PollServiceImpl implements PollService {

    private final PollRepository repository;

    @Autowired
    public PollServiceImpl( PollRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Poll> getLastPoll(User user) {
       return repository.findFirstByUserOrderByIdDesc(user);
    }

}
