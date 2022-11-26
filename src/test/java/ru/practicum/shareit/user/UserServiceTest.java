package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.common.exceptions.UserNotFoundException;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTest {
    private User user1;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .email("user1@user.com")
                .name("user1")
                .build();
    }

    @Test
    void createTest() {
        userService.create(user1);
        assertEquals(1, userService.findAll().size());
        assertEquals(user1, userService.findById(1L));
    }

    @Test
    void updatePartialTest() {
        userService.create(user1);
        User userPartial = User.builder()
                .id(1L)
                .name("updated name")
                .build();
        userService.update(userPartial);
        assertEquals(user1.getEmail(), userService.findById(1L).getEmail());
        assertEquals("updated name", userService.findById(1L).getName());
    }

    @Test
    void updateNonExistingTest() {
        assertThrows(UserNotFoundException.class, () -> userService.update(user1));
    }

    @Test
    void findNonExistingTest() {
        assertThrows(UserNotFoundException.class, () -> userService.findById(-1L));
    }
}