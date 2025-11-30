package com.aptBooker.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    // User registration endpoint
    @PostMapping("/register")
    public Object registerUser(@RequestParam String name, @RequestParam String email, @RequestParam String password) {
        return userService.registerUser(name, email, password);
    }

    // User login endpoint
    @PostMapping("/login")
    public Object loginUser(@RequestParam String email, @RequestParam String password) {
        return userService.loginUser(email, password);
    }
}
