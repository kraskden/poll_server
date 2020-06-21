package org.kraskovsky.polls.service;

import org.kraskovsky.polls.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(User user);
    List<User> getAll();

    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);

    void deleteById(Long id);

    Boolean changePassword(User user, String current, String updated);
    void updateProfile(User current, User updated);
}
