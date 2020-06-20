package org.kraskovsky.polls.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kraskovsky.polls.model.*;
import org.kraskovsky.polls.repository.PollAnswerRepository;
import org.kraskovsky.polls.repository.PollRepository;
import org.kraskovsky.polls.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnswerServiceImpl implements AnswerService {

    private final PollAnswerRepository pollAnswerRepository;
    private final PollRepository pollRepository;

    @Autowired
    public AnswerServiceImpl(PollAnswerRepository pollAnswerRepository, PollRepository pollRepository) {
        this.pollAnswerRepository = pollAnswerRepository;
        this.pollRepository = pollRepository;
    }

    @Override
    public Boolean submit(Poll poll, Map<String, String> answer) {
        log.info("IN SUBMIT -- STARTED");
        log.info("ANSWER is -- {}", answer);

        List<Field> spec = poll.getFields();
        Optional<Map<Field, String>> optionalRecord =  validate(spec, answer);
        if (optionalRecord.isEmpty()) {
            return false;
        }
        log.info("IN SUBMIT -- RECORD VALID");

        Map<Field, String> record = optionalRecord.get();

        PollAnswer pollAnswer = new PollAnswer();
        pollAnswer.setPoll(poll);

        List<FieldAnswer> answers = new ArrayList<>();
        //
        record.forEach((field, answerToField) -> {
            FieldAnswer fieldAnswer = new FieldAnswer();
            fieldAnswer.setPollAnswer(pollAnswer);
            fieldAnswer.setField(field);
            fieldAnswer.setAnswer(answerToField);

            answers.add(fieldAnswer);
        });

        pollAnswer.setAnswers(answers);
        pollAnswerRepository.save(pollAnswer);

        poll.setAnswersCount(poll.getAnswersCount() + 1);
        pollRepository.save(poll);

        return true;
    }

    @Override
    public List<Map<String, String>> getAnswers(Poll poll) {
        List<PollAnswer> answers = pollAnswerRepository.getAllByPoll(poll);
        return  answers.stream()
                .map(answer -> {
                    Map<String, String> record = new HashMap<>();
                    for (FieldAnswer fieldAnswer : answer.getAnswers()) {
                        record.put(fieldAnswer.getField().getName(), fieldAnswer.getAnswer());
                    }
                    return record;
                }).collect(Collectors.toList());
    }

    private Boolean checkDateValid(String date) {
        try {
            return Integer.parseInt(date) > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private Boolean checkIterValid(List<FieldProperty> spec, String value) {
        return spec.stream()
                .map(FieldProperty::getName).anyMatch(propName -> propName.equals(value));
    }

    private String normalizeBoolean(String bool) {
        return Boolean.parseBoolean(bool) ? "TRUE": "FALSE";
    }

    private Boolean isValidFieldAnswer(Field field, String answer) {
        switch (field.getFieldType()) {
            case SINGLE_TEXT:
                return answer.indexOf('\n') == -1;

            case MULTI_TEXT:
                return true;

            case CHECKBOX:
                return true;

            case DATE:
                return checkDateValid(answer);

            case COMBOBOX:
            case RADIO_BUTTON:
                return checkIterValid(field.getProperties(), answer);

            default:
                return true;
        }
    }

    private Optional<Map<Field, String>> validate(List<Field> spec, Map<String, String> data) {
        Map<Field, String> res = new HashMap<>();

        log.info("Start validating");

        for (Field field : spec) {
            log.info("Start validating field {}", field.getName());
            String answer = data.get(field.getName());
            if (answer == null) {
                log.info("Answer for field - {} is null", field.getName());
                if (!field.getIsRequired() || !field.getIsActive()) {
                    continue;
                } else {
                    return Optional.empty();
                }
            }
            if (isValidFieldAnswer(field,answer)) {
                log.info("Field {} is valid", field.getName());
                if (field.getFieldType() == FieldType.CHECKBOX) {
                    res.put(field, normalizeBoolean(answer));
                } else {
                    res.put(field, answer);
                }
            } else {
                log.warn("Field {} is invalid", field.getName());
                return Optional.empty();
            }
        }
        return Optional.of(res);
    }

}