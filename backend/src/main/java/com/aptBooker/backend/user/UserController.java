package com.aptBooker.backend.user;

import com.aptBooker.backend.user.dto.request.UserLoginDto;
import com.aptBooker.backend.user.dto.request.UserRegistrationDto;
import com.aptBooker.backend.user.dto.response.AuthResponseDto;
import com.aptBooker.backend.user.dto.response.UserDto;
import com.aptBooker.backend.user.dto.response.UserErrorDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;


    // User registration endpoint
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            UserEntity user = userService.registerUser(userRegistrationDto);

            //convert entity to dto
            UserDto userDto = new UserDto();
            userDto.setName(user.getName());
            userDto.setId(user.getId());
            userDto.setEmail(user.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(userDto);

        } catch (RuntimeException e) {
            UserErrorDto errorDto = new UserErrorDto();
            errorDto.setErrorMessage(e.getMessage());
            errorDto.setErrorCode("REGISTRATION_FAILED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
        }
    }

    // User login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginDto userLoginDto) {
        try {
            AuthResponseDto authResponse = userService.loginUser(userLoginDto);
            return ResponseEntity.ok(authResponse);

        } catch (RuntimeException e) {
            UserErrorDto errorDto = new UserErrorDto();
            errorDto.setErrorMessage(e.getMessage());
            errorDto.setErrorCode("LOGIN_FAILED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);
        }
    }
}
