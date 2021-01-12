package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.util.List;

public interface UserDAO {

    List<User> findAll();

    User findByUsername(String username);

    long findIdByUsername(String username);

    boolean create(String username, String password);
    
    public String findUsernameById(long id);
}
