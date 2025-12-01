package com.aptBooker.backend.user;

import com.aptBooker.backend.security.JwtUtil;
import com.aptBooker.backend.user.dto.response.AuthResponseDto;
import com.aptBooker.backend.user.dto.response.UserDto;
import com.aptBooker.backend.user.dto.response.UserErrorDto;
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
    public Object registerUser(UserRegistrationDto userRegistrationDto){

        // extract user details from dto
        //extrair detalhes do usuario do dto
        String name = userRegistrationDto.getName();
        String email = userRegistrationDto.getEmail();
        String password = userRegistrationDto.getPassword();
        String confirmPassword = userRegistrationDto.getConfirmPassword();
        String role = userRegistrationDto.getRole();

        //check if pw match
        //verificar se as senhas coincidem
        if (!password.equals(confirmPassword)){
            //return error dto
            //retornar dto de erro
            UserErrorDto errorDto = new UserErrorDto();
            errorDto.setErrorMessage("Passwords do not match");
            errorDto.setErrorCode("PASSWORD_MISMATCH");
            return errorDto;
        }

        //check if user already exists, if not create new user
        //checar se o usuario ja existe, se nao criar novo usuario
        if (!userRepository.existsByEmail(email)) {
            UserEntity newUser = new UserEntity();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setRole(role);
            userRepository.save(newUser);

            //return user dto success message
            //retornar dto de usuario com mensagem de sucesso
            UserDto userDto = new UserDto();
            userDto.setId(newUser.getId());
            userDto.setName(newUser.getName());
            userDto.setEmail(newUser.getEmail());
            return userDto;


        } else {
            //return error dto if user already exists
            //retornar dto de erro se o usuario ja existir
            UserErrorDto errorDto = new UserErrorDto();
            errorDto.setErrorMessage("Email is already taken");
            errorDto.setErrorCode("USER_EXISTS");
            return errorDto;
        }
    }


    //login user
    public Object loginUser(UserLoginDto userLoginDto){

        // extract user details from dto
        //extrair detalhes do usuario do dto
        String email = userLoginDto.getEmail();
        String password = userLoginDto.getPassword();

        //check if user exists
        //checar se o usuario existe
        Optional<UserEntity> user = userRepository.findByEmail(email);

        if (user.isEmpty()){

//            return error dto
            //retornar dto de erro
            UserErrorDto errorDto = new UserErrorDto();
            errorDto.setErrorMessage("User not found");
            errorDto.setErrorCode("USER_NOT_FOUND");

            return errorDto;
        }

            //check if pw is correct
        //checar se a senha esta correta
        if(user.get().getPassword().equals(password)){

            //generate jwt token
            //gerar token jwt
            String token = jwtUtil.generateToken(
                    user.get().getId(),
                    user.get().getName(),
                    user.get().getEmail()
            );

            return new AuthResponseDto(
                    token,
                    user.get().getId(),
                    user.get().getName(),
                    user.get().getEmail(),
                    user.get().getRole()
            );
        } else {
            //return error dto
            //retornar dto de erro
            UserErrorDto errorDto = new UserErrorDto();
            errorDto.setErrorMessage("Incorrect password");
            errorDto.setErrorCode("INCORRECT_PASSWORD");

            return errorDto;
        }
    }
}
