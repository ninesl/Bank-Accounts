package com.tenmo.dao;

import com.tenmo.model.User;

import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findByUsername(String username);

    public User findUserByAccountId (long accountId);

    int findIdByUsername(String username);

    User findByUserId (long userId);

    boolean create(String username, String password);

}
