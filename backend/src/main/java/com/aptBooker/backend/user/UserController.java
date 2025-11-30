package com.aptBooker.backend.user;

import com.aptBooker.backend.user.dto.request.UserLoginDto;
import com.aptBooker.backend.user.dto.request.UserRegistrationDto;
import com.aptBooker.backend.user.dto.response.UserErrorDto;
import com.aptBooker.backend.user.dto.response.UserDto;
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
        Object result = userService.registerUser(userRegistrationDto);
        if (result instanceof UserErrorDto) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.ok(result);
    }

    // User login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginDto userLoginDto) {
        Object result = userService.loginUser(userLoginDto);
        if (result instanceof UserErrorDto) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
        return ResponseEntity.ok(result);
    }
}
