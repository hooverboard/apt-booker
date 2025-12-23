package com.aptBooker.backend.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void UserRepository_FindByEmail_ReturnUser() {

        //arrange
        UserEntity user = UserEntity.builder()
                .name("test user")
                .email("testuser@email.com")
                .password("testpassword")
                .role("user")
                .build();
        userRepository.save(user);

        //act
        Optional<UserEntity> foundUser = userRepository.findByEmail("testuser@email.com");
        Optional<UserEntity> userNotFound = userRepository.findByEmail("wrong@email.co");

        //assert
        assertTrue(foundUser.isPresent());
        assertEquals("testuser@email.com", foundUser.get().getEmail());

        assertTrue(userNotFound.isEmpty());
    }

    @Test
    void UserRepository_ExistsByEmail_ReturnTrueorFalse(){

        //arrange
        UserEntity user = UserEntity.builder()
                .name("test user")
                .email("testuser@email.com")
                .password("testpassword")
                .role("user")
                .build();
        userRepository.save(user);
        //act
        boolean foundUser = userRepository.existsByEmail("testuser@email.com");
        boolean userNotFound = userRepository.existsByEmail("wrong@email.com");

        //assert
        assertTrue(foundUser);
        assertFalse(userNotFound);
    }
}