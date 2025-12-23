package com.aptBooker.backend.user;

import com.aptBooker.backend.exceptions.InvalidCredentialsException;
import com.aptBooker.backend.exceptions.MismatchPasswordException;
import com.aptBooker.backend.exceptions.ResourceNotFoundException;
import com.aptBooker.backend.exceptions.UserAlreadyExistsException;
import com.aptBooker.backend.user.dto.request.UserLoginDto;
import com.aptBooker.backend.user.dto.request.UserRegistrationDto;
import com.aptBooker.backend.user.dto.response.AuthResponseDto;
import com.aptBooker.backend.user.dto.response.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    //registration tests
    @Test
    void registerUser_success_return201() throws Exception {
        UserRegistrationDto request = new UserRegistrationDto();
        request.setName("Test User");
        request.setEmail("test@email.com");
        request.setPassword("password");
        request.setConfirmPassword("password");
        request.setRole("user");

        UserDto response = new UserDto();
        response.setId(1L);
        response.setName("Test User");
        response.setEmail("test@email.com");

        when(userService.registerUser(Mockito.any(UserRegistrationDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.id").value(1L))
                .andExpect((ResultMatcher) jsonPath("$.name").value("Test User"))
                .andExpect((ResultMatcher) jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void registerUser_whenEmailIsTaken_return409() throws Exception {
        UserRegistrationDto request = new UserRegistrationDto();
        request.setName("Test User");
        request.setEmail("test@email.com");
        request.setPassword("password");
        request.setConfirmPassword("password");
        request.setRole("user");

        when(userService.registerUser(Mockito.any(UserRegistrationDto.class)))
                .thenThrow(new UserAlreadyExistsException("Email already taken"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void registerUser_whenPasswordMismatch_returnError() throws Exception {
        UserRegistrationDto request = new UserRegistrationDto();
        request.setName("Test User");
        request.setEmail("test@email.com");
        request.setPassword("password");
        request.setConfirmPassword("password2");
        request.setRole("user");

        when(userService.registerUser(Mockito.any(UserRegistrationDto.class)))
                .thenThrow(new MismatchPasswordException("Passwords do not match"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());
    }

    //login tests
    @Test
    void loginUser_success_returns201() throws Exception {
        UserLoginDto request = new UserLoginDto();
        request.setEmail("test@email.com");
        request.setPassword("password");

        AuthResponseDto response = new AuthResponseDto();
        response.setEmail("test@email.com");
        response.setName("Test user");
        response.setId(1L);
        response.setRole("user");
        response.setToken("fake-token");

        when(userService.loginUser(Mockito.any(UserLoginDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.email").value("test@email.com"))
                .andExpect((ResultMatcher) jsonPath("$.name").value("Test user"))
                .andExpect((ResultMatcher) jsonPath("$.id").value(1L))
                .andExpect((ResultMatcher) jsonPath("$.role").value("user"))
                .andExpect((ResultMatcher) jsonPath("$.token").value("fake-token"));
    }

    @Test
    void loginUser_whenUserNotFound_returnsNOTFOUND() throws Exception {

        UserLoginDto request = new UserLoginDto();
        request.setEmail("test@email.com");
        request.setPassword("password");

        when(userService.loginUser(Mockito.any(UserLoginDto.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void loginUser_whenIncorrectPassword_returnsUNAUTHORIZED() throws Exception {

        UserLoginDto request = new UserLoginDto();
        request.setEmail("test@email.com");
        request.setPassword("password");

        when(userService.loginUser(Mockito.any(UserLoginDto.class)))
                .thenThrow(new InvalidCredentialsException("Incorrect pw"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isUnauthorized());
    }
}