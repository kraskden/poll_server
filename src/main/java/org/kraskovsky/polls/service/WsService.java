package org.kraskovsky.polls.service;

import org.kraskovsky.polls.model.User;

import java.util.Map;

public interface WsService {
    void sendMsg(User user, String dest, Object payload);

    void sendAnswerToUser(User user, Map<String, String> answer);
}
