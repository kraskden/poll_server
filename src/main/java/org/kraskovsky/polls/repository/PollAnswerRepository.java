package org.kraskovsky.polls.repository;

import org.kraskovsky.polls.model.Poll;
import org.kraskovsky.polls.model.PollAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollAnswerRepository extends JpaRepository<PollAnswer, Long> {
    List<PollAnswer> getAllByPoll(Poll poll);
}
