package org.kraskovsky.polls.service;

import org.kraskovsky.polls.model.Poll;

import java.util.List;
import java.util.Map;

public interface AnswerService {
    Boolean submit(Poll poll, Map<String, String> answer);
    List<Map<String, String>> getAnswers(Poll poll);
}