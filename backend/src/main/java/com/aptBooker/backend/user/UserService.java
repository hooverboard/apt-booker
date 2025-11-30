package com.aptBooker.backend.user;

import com.aptBooker.backend.user.dto.response.UserDto;
import com.aptBooker.backend.user.dto.response.UserErrorDto;
import com.aptBooker.backend.user.dto.request.UserLoginDto;
import com.aptBooker.backend.user.dto.request.UserRegistrationDto;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // user registration
    public Object registerUser(UserRegistrationDto userRegistrationDto){

        // extract user details from dto
        String name = userRegistrationDto.getName();
        String email = userRegistrationDto.getEmail();
        String password = userRegistrationDto.getPassword();
        String confirmPassword = userRegistrationDto.getConfirmPassword();
        String role = userRegistrationDto.getRole();

        //check if pw match
        if (!password.equals(confirmPassword)){
            //return error dto
            UserErrorDto errorDto = new UserErrorDto();
            errorDto.setErrorMessage("Passwords do not match");
            errorDto.setErrorCode("PASSWORD_MISMATCH");
            return errorDto;
        }

        //check if user already exists, if not create new user
        if (!userRepository.existsByEmail(email)) {
            UserEntity newUser = new UserEntity();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setRole(role);
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
    public Object loginUser(UserLoginDto userLoginDto){

        // extract user details from dto
        String email = userLoginDto.getEmail();
        String password = userLoginDto.getPassword();

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
