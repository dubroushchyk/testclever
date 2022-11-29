package com.dubroushchyk.clever.services;

import com.dubroushchyk.clever.entities.User;

public interface UserService {

    User findByLoginOrCreate(String login);

}
