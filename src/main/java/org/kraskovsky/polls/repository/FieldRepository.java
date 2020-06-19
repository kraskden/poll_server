package org.kraskovsky.polls.repository;

import org.kraskovsky.polls.model.Field;
import org.kraskovsky.polls.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {
    List<Field>  findAllByPoll(Poll poll);
    void removeFieldByPollAndName(Poll poll, String name);
}