package com.aptBooker.backend.user;

import com.aptBooker.backend.user.dto.UserDto;
import com.aptBooker.backend.user.dto.UserErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // user registration
    public Object registerUser(String name, String email, String password){

        //check if user already exists, if not create new user
        if (!userRepository.existsByEmail(email)) {
            UserEntity newUser = new UserEntity();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(password);
            userRepository.save(newUser);

            //return user dto success message
            UserDto userDto = new UserDto();
            userDto.setId(newUser.getId());
            userDto.setName(newUser.getName());
            userDto.setEmail(newUser.getEmail());
            return userDto;


        } else {
            //return error dto if user already exists
            UserErrorDto errorDto = new UserErrorDto();
            errorDto.setErrorMessage("Email is already taken");
            errorDto.setErrorCode("USER_EXISTS");
            return errorDto;
        }
    }


    //login user
    public Object loginUser(String email, String password){

        //check if user exists
        Optional<UserEntity> user = userRepository.findByEmail(email);

        if (user.isEmpty()){

//            return error dto
            UserErrorDto errorDto = new UserErrorDto();
            errorDto.setErrorMessage("User not found");
            errorDto.setErrorCode("USER_NOT_FOUND");

            return errorDto;
        }

            //check if pw is correct
        if(user.get().getPassword().equals(password)){

            //return success dto
            UserDto userDto = new UserDto();
            userDto.setEmail(user.get().getEmail());
            userDto.setId(user.get().getId());
            userDto.setName(user.get().getName());

            return userDto;
        } else {
            //return error dto
            UserErrorDto errorDto = new UserErrorDto();
            errorDto.setErrorMessage("Incorrect password");
            errorDto.setErrorCode("INCORRECT_PASSWORD");

            return errorDto;
        }
    }
}
