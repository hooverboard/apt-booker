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
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {

        UserDto createdUser = userService.registerUser(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // User login endpoint
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody UserLoginDto userLoginDto) {

        AuthResponseDto authResponse = userService.loginUser(userLoginDto);
        return ResponseEntity.ok(authResponse);
    }
}
