package com.dengzhanglin.favoriteapi.controller;

import com.dengzhanglin.favoriteapi.domain.User;
import com.dengzhanglin.favoriteapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/user")
public class UserController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // register
    @PostMapping(value = "/register")
    public User register(@RequestBody User user) {
        this.logger.debug("user register: " + user.toString());
        user = this.userService.register(user.getEmail(), user.getPassword());
        if (user == null) {
            return new User();
        }
        return user;
    }

    // login
    @PostMapping(value = "/login")
    public User login(@RequestBody User user) {
        return user;
    }

    // info
    @GetMapping(value = "/info/{id}")
    public Optional<User> info(@PathVariable("id") Long id) {
        return  this.userService.findById(id);
    }

    @GetMapping(value = "/test")
    public Object test() {
        return this.success();
    }
}
