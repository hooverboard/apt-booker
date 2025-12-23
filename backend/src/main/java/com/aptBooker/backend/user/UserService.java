package com.aptBooker.backend.user;

import com.aptBooker.backend.exceptions.InvalidCredentialsException;
import com.aptBooker.backend.exceptions.MismatchPasswordException;
import com.aptBooker.backend.exceptions.ResourceNotFoundException;
import com.aptBooker.backend.exceptions.UserAlreadyExistsException;
import com.aptBooker.backend.security.JwtUtil;
import com.aptBooker.backend.user.dto.response.AuthResponseDto;
import com.aptBooker.backend.user.dto.response.UserDto;
import com.aptBooker.backend.user.dto.request.UserLoginDto;
import com.aptBooker.backend.user.dto.request.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // user registration
    // registrar usuario
    public UserDto registerUser(UserRegistrationDto userRegistrationDto){

        // extract user details from dto
        //extrair detalhes do usuario do dto
        String name = userRegistrationDto.getName();
        String email = userRegistrationDto.getEmail().toLowerCase();
        String password = userRegistrationDto.getPassword();
        String confirmPassword = userRegistrationDto.getConfirmPassword();
        String role = userRegistrationDto.getRole();

        //check if pw match
        //verificar se as senhas coincidem
        if (!password.equals(confirmPassword)){
            throw new MismatchPasswordException("Passwords do not match");
        }

        //check if user already exists
        //verificar se o usuario ja existe
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email is already taken");
        }

        //create new user
        //criar novo usuario
        UserEntity newUser = new UserEntity();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(role);
        UserEntity savedUser = userRepository.save(newUser);

        UserDto userDto = new UserDto();
        userDto.setId(savedUser.getId());
        userDto.setName(savedUser.getName());
        userDto.setEmail(savedUser.getEmail());
        return userDto;
    }


    //login user
    public AuthResponseDto loginUser(UserLoginDto userLoginDto){

        // extract user details from dto
        //extrair detalhes do usuario do dto
        String email = userLoginDto.getEmail().toLowerCase();
        String password = userLoginDto.getPassword();

        //check if user exists
        //checar se o usuario existe
        Optional<UserEntity> user = userRepository.findByEmail(email);

        if (user.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }

        //check if pw is correct
        //checar se a senha esta correta
        if(!user.get().getPassword().equals(password)){
            throw new InvalidCredentialsException("Incorrect password");
        }

        //generate jwt token
        //gerar token jwt
        String token = jwtUtil.generateToken(
                user.get().getId(),
                user.get().getName(),
                user.get().getEmail(),
                user.get().getRole()
        );

        return new AuthResponseDto(
                token,
                user.get().getId(),
                user.get().getName(),
                user.get().getEmail(),
                user.get().getRole()
        );
    }
}
