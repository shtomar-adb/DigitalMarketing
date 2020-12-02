package com.digital.marketing.auth.service;

import com.digital.marketing.auth.model.User;

public interface UserService {

    public User findUserByEmail(String email);

    public void saveUser(User user);

}
