package com.optical.manage.service;

import com.optical.manage.DO.User;

public interface UserService {
    Long createUser(User user);
    User getUserById(Long id);
    User getUserByName(String name);
    boolean updateUser(User user);
    boolean deleteUser(Long id);
    boolean validatePassword(String rawPassword, String encodedPassword);
}
