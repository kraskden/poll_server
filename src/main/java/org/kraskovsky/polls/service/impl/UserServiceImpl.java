package org.kraskovsky.polls.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.repository.UserRepository;
import org.kraskovsky.polls.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saveUser =  userRepository.save(user);

        log.info("User {} registered", saveUser);
        return saveUser;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public String resetPassword(User user) {
        String newPassword = RandomString.make(10);
        user.setPassword(passwordEncoder.encode(newPassword));
        return newPassword;
    }

    @Override
    public Boolean changePassword(User user, String current, String updated) {
        String original = user.getPassword();

        if (!BCrypt.checkpw(current, original)) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(updated));
        userRepository.save(user);
        return true;
    }

    @Override
    public void updateProfile(User current, User updated) {
        current.setEmail(updated.getEmail());
        current.setFirstName(updated.getFirstName());
        current.setSecondName(updated.getSecondName());
        current.setPhone(updated.getPhone());

        userRepository.save(current);
    }

    @Override
    public Optional<User> getUserFromSecurityContext() {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails details = (UserDetails)obj;

        return this.findByEmail(details.getUsername());
    }
}
