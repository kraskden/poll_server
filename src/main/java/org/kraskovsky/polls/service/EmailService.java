package org.kraskovsky.polls.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subj, String content);
}
