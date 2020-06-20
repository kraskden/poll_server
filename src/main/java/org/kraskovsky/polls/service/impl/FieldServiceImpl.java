package org.kraskovsky.polls.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kraskovsky.polls.model.Field;
import org.kraskovsky.polls.model.FieldProperty;
import org.kraskovsky.polls.model.Poll;
import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.repository.FieldPropertyRepository;
import org.kraskovsky.polls.repository.FieldRepository;
import org.kraskovsky.polls.repository.PollRepository;
import org.kraskovsky.polls.service.FieldService;
import org.kraskovsky.polls.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final PollRepository pollRepository;
    private final PollService pollService;
    private final FieldPropertyRepository fieldPropertyRepository;

    @Autowired
    public FieldServiceImpl(FieldRepository fieldRepository, PollRepository pollRepository, PollService pollService, FieldPropertyRepository fieldPropertyRepository) {
        this.fieldRepository = fieldRepository;
        this.pollRepository = pollRepository;
        this.pollService = pollService;
        this.fieldPropertyRepository = fieldPropertyRepository;
    }

    private void migratePoll(Poll oldPoll, Poll newPoll) {
        List<Field> newList = new ArrayList<>();
        for (Field f : oldPoll.getFields()) {
            Field newField = new Field(
                    f.getName(),
                    f.getIsActive(),
                    f.getIsRequired(),
                    f.getFieldType()
            );

            List<FieldProperty> newProperties = new ArrayList<>();
            for (FieldProperty prop : f.getProperties()) {
                newProperties.add(new FieldProperty(prop.getName(), newField));
            }
            newField.setProperties(newProperties);

            newField.setPoll(newPoll);
            newList.add(newField);
        }
        newPoll.setFields(newList);
    }

    private Poll getNewPoll(User user) {
        Optional<Poll> poll = pollService.getLastPoll(user);
        Poll updatedPoll;
        if (poll.isPresent() && poll.get().getAnswersCount() == 0) {
            log.info("[Field] Return Current poll");
            updatedPoll = poll.get();
        } else {
            log.info("[Field] Return New poll");
            updatedPoll = new Poll(user);
            poll.ifPresent(oldPoll -> migratePoll(oldPoll, updatedPoll));
        }

        return updatedPoll;
    }

    @Override
    public void addField(User user, Field field) {
        Poll updatedPoll = getNewPoll(user);

        field.setPoll(updatedPoll);
        updatedPoll.getFields().add(field);
        pollRepository.save(updatedPoll);
    }

    @Override
    public List<Field> getFields(User user) {
        Optional<Poll> poll = pollService.getLastPoll(user);
        if (poll.isPresent()) {
            return fieldRepository.findAllByPoll(poll.get());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void removeField(User user, Long id) {
        Poll updatedPoll = getNewPoll(user);
        pollRepository.save(updatedPoll);
        fieldRepository.removeFieldByPollAndId(updatedPoll, id);
    }

    @Override
    public void updateField(User user, Long id, Field field) {
        Optional<Poll> optionalLastPoll = pollService.getLastPoll(user);
        if (optionalLastPoll.isEmpty()) {
            return ;
        }
        Poll lastPoll = optionalLastPoll.get();

        if (lastPoll.getAnswersCount() == 0) {
            // update in place
            fieldRepository.findFirstByIdAndPoll(id, lastPoll).ifPresent(updField -> {
                fieldPropertyRepository.removeAllByField(updField);
                field.setPoll(lastPoll);
                field.setId(updField.getId());
                fieldRepository.save(field);
            });
            return;
        }

        // create new poll and update it;
        List<Field> lastPollFields = lastPoll.getFields();
        for (int i = 0; i < lastPollFields.size(); ++i) {
            if (lastPollFields.get(i).getId().equals(id)) {
                Poll updatedPoll = getNewPoll(user);
                field.setPoll(updatedPoll);
                updatedPoll.getFields().set(i, field);
                log.info("Updated field - {}", updatedPoll.getFields());
                pollRepository.save(updatedPoll);
            }
        }
    }
}
