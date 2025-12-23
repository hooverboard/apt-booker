package com.aptBooker.backend.user;

import com.aptBooker.backend.exceptions.InvalidCredentialsException;
import com.aptBooker.backend.exceptions.MismatchPasswordException;
import com.aptBooker.backend.exceptions.ResourceNotFoundException;
import com.aptBooker.backend.exceptions.UserAlreadyExistsException;
import com.aptBooker.backend.security.JwtUtil;
import com.aptBooker.backend.user.dto.request.UserLoginDto;
import com.aptBooker.backend.user.dto.request.UserRegistrationDto;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.module.ResolutionException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    UserService userService;

    UserEntity user;
    UserLoginDto userLoginDto;
    UserRegistrationDto userRegistrationDto;

    //test setup
    @BeforeEach
    void setup(){
        userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setName("Test User");
        userRegistrationDto.setEmail("test@email.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setConfirmPassword("password");
        userRegistrationDto.setRole("user");

        userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("test@email.com");
        userLoginDto.setPassword("password");

        user = new UserEntity();
        user.setName("Test User");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole("user");
        user.setId(1L);
    }

    //registration tests
    @Test
    void registerUser_whenEmailIsNotTaken_returnsUserDTO() {

        when(userRepository.existsByEmail("test@email.com"))
                .thenReturn(false);

        when(userRepository.save(Mockito.any(UserEntity.class)))
                .thenReturn(user);

        var result = userService.registerUser(userRegistrationDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    void registerUser_whenEmailAlreadyTaken_throwsUserAlreadyExistsException(){

        when(userRepository.existsByEmail("test@email.com"))
                .thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(userRegistrationDto));
    }

    @Test
    void registerUser_whenPasswordsDoNotMatch_throwsMismatchPasswordException(){
        userRegistrationDto.setConfirmPassword("wrongPassword");

        assertThrows(MismatchPasswordException.class,
                () -> userService.registerUser(userRegistrationDto));
    }


    //login tests
    @Test
    void loginUser_success_returnAuthResponseDTO() {

        when(userRepository.findByEmail("test@email.com"))
                .thenReturn(Optional.ofNullable(user));

        when(jwtUtil.generateToken(
                1L,
                "Test User",
                "test@email.com",
                "user"
        )).thenReturn("fake-jwt-token");

        var result = userService.loginUser(userLoginDto);

        assertNotNull(result);
        assertEquals("fake-jwt-token", result.getToken());
        assertEquals(1L, result.getId());
        assertEquals("user", result.getRole());
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    void loginUser_whenUserNotFound_throwResourceNotFoundException(){

        when(userRepository.findByEmail("test@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.loginUser(userLoginDto));
    }

    @Test
    void loginUser_whenIncorrectPassword_throwInvalidCredentialsException(){

        user.setPassword("differentPW");
        when(userRepository.findByEmail("test@email.com"))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(InvalidCredentialsException.class,
                () -> userService.loginUser(userLoginDto));
    }
}