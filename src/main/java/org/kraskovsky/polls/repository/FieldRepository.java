package org.kraskovsky.polls.repository;

import org.kraskovsky.polls.model.Field;
import org.kraskovsky.polls.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {
    List<Field>  findAllByPollOrderById(Poll poll);
    Optional<Field> findFirstByIdAndPoll(Long Id, Poll poll);
    void removeFieldByPollAndId(Poll poll, Long id);
}
