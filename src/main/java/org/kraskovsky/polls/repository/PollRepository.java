package org.kraskovsky.polls.repository;

import org.kraskovsky.polls.model.Poll;
import org.kraskovsky.polls.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    Optional<Poll> findFirstByUserOrderByIdDesc(User user);
    Optional<Poll> findFirstByUserAndId(User user, Long id);
    List<Poll> getAllByUser(User user);
}
