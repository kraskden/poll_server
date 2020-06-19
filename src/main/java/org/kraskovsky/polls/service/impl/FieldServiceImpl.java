package org.kraskovsky.polls.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.SpecialOneToOneType;
import org.kraskovsky.polls.model.Field;
import org.kraskovsky.polls.model.FieldProperty;
import org.kraskovsky.polls.model.Poll;
import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.repository.FieldRepository;
import org.kraskovsky.polls.repository.PollRepository;
import org.kraskovsky.polls.service.FieldService;
import org.kraskovsky.polls.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final PollRepository pollRepository;
    private final PollService pollService;

    @Autowired
    public FieldServiceImpl(FieldRepository fieldRepository, PollRepository pollRepository, PollService pollService) {
        this.fieldRepository = fieldRepository;
        this.pollRepository = pollRepository;
        this.pollService = pollService;
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
                newProperties.add(new FieldProperty(f.getName(), newField));
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
            updatedPoll = poll.get();
        } else {
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
    public void removeField(User user, String name) {
        Poll updatedPoll = getNewPoll(user);
        pollRepository.save(updatedPoll);
        fieldRepository.removeFieldByPollAndName(updatedPoll, name);
    }

    @Override
    public void updateField(User user, Field field) {
        Poll updatedPoll = getNewPoll(user);
        List<Field> fields =  updatedPoll.getFields();

        field.setPoll(updatedPoll);

        for (int i = 0; i < fields.size(); ++i) {
            if (fields.get(i).getName().equals(field.getName())) {
                fields.set(i, field);
            }
        }

        pollRepository.save(updatedPoll);

    }
}
