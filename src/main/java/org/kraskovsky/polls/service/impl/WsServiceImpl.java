package org.kraskovsky.polls.service.impl;

import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.service.WsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WsServiceImpl implements WsService {

    private final SimpMessagingTemplate template;

    @Autowired
    public WsServiceImpl(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void sendMsg(User user, String dest, Object payload) {
        template.convertAndSendToUser(user.getEmail(), dest, payload);
    }

    @Override
    public void sendAnswerToUser(User user, Map<String, String> answer) {
        this.sendMsg(user, "/user/topic/answers", answer);
    }
}
