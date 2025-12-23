package com.aptBooker.backend.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

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

        //act
        UserEntity savedUser = userRepository.save(user);

        //assert
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId() > 0);
    }
}