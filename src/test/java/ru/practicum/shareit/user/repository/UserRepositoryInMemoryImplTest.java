package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.common.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRepositoryInMemoryImplTest {
    private User user1;
    private User user2;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .email("user1@user.com")
                .name("user1")
                .build();
        user2 = User.builder()
                .id(2L)
                .email("user2@user.com")
                .name("user2")
                .build();
    }

    @Test
    void createTest() {
        userRepository.save(user1);
        assertEquals(1, userRepository.findAll().size());
        assertEquals(Optional.of(user1), userRepository.findById(1L));
    }

    @Test
    void updateTest() {
        userRepository.save(user1);
        user1.setEmail("updated@user.com");
        user1.setName("updated");
        userRepository.update(user1);
        assertEquals(1, userRepository.findAll().size());
        assertEquals(Optional.of(user1), userRepository.findById(1L));
    }

    @Test
    void deleteTest() {
        userRepository.save(user1);
        userRepository.save(user2);
        assertEquals(2, userRepository.findAll().size());
        userRepository.deleteById(1L);
        assertEquals(1, userRepository.findAll().size());
        assertEquals(List.of(user2), userRepository.findAll());
    }

    @Test
    void deleteNonExistingTest() {
        assertThrows(UserNotFoundException.class, () -> userRepository.deleteById(-1L));
    }

    @Test
    void findByEmailTest() {
        userRepository.save(user1);
        userRepository.save(user2);
        assertEquals(Optional.of(user1), userRepository.findByEmail(user1.getEmail()));
    }
}