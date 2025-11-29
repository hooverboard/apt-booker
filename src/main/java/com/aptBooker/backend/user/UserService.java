package com.aptBooker.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // user registration
    public void registerUser(String name, String email, String password){

        //check if user already exists
        if (!userRepository.existsByEmail(email)) {
            UserEntity newUser = new UserEntity();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(password);
            userRepository.save(newUser);
        }
    }
}
