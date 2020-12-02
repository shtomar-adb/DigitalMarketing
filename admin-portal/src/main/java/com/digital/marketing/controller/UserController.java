package com.digital.marketing.controller;

import com.digital.marketing.entity.User;
import com.digital.marketing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users")
    public User saveUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") String userId) {
        return userRepository.getUserById(userId);
    }

    @GetMapping("/users")
    public List<User> getAllUserers() {
        return userRepository.getAllUsers();
    }
}
