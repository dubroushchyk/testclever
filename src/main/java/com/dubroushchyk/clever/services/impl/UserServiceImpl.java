package com.dubroushchyk.clever.services.impl;

import com.dubroushchyk.clever.entities.User;
import com.dubroushchyk.clever.repository.UserRepository;
import com.dubroushchyk.clever.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Override
    public User findByLoginOrCreate(String login) {
        User user = null;
        try {
            user = getByLogin(login).orElseGet(() -> create(login));
        } catch (Exception e) {
            log.info("User with login: {} not created", login);
        }
        return user;
    }

    private Optional<User> getByLogin(String login) {
        return userRepo.getByLogin(login);
    }

    private User create(String login) {
        return userRepo.save(new User(login));
    }

}
