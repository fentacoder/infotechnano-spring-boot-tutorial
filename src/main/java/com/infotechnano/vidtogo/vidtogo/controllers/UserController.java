package com.infotechnano.vidtogo.vidtogo.controllers;

import com.infotechnano.vidtogo.vidtogo.models.User;
import com.infotechnano.vidtogo.vidtogo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping(path = "login/{userId}")
    public User logIn(@PathVariable("userId") UUID userId){
        return userService.loginUser(userId);
    }

    @PostMapping(path = "register/{userId}")
    public User register(@RequestBody @Validated User user){
        return userService.registerUser(user);
    }

    @PostMapping(path = "update/{userId}")
    public Integer updateStudent(@PathVariable("userId") UUID userId,
                              @RequestBody User user) {
        return userService.updateUser(userId, user);
    }

    @PostMapping("delete/{userId}")
    public Integer deleteStudent(@PathVariable("userId") UUID userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping("checkpassword/{userPassword}")
    public boolean checkPassword(@PathVariable("userPassword") String userPassword){
        return userService.checkPassword(userPassword);
    }
}
