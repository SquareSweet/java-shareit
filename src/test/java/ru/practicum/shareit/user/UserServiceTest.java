package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.common.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    private User user1;

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository mockRepository;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .email("user1@user.com")
                .name("user1")
                .build();
        Mockito.when(mockRepository.save(user1)).thenReturn(user1);
        Mockito.when(mockRepository.findById(1L)).thenReturn(Optional.of(user1));
    }

    @Test
    void createTest() {
        assertEquals(user1, userService.create(user1));
        Mockito.verify(mockRepository, Mockito.times(1)).save(user1);
    }

    @Test
    void findTest() {
        assertEquals(user1, userService.findById(1L));
        Mockito.verify(mockRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void findAll() {
        List<User> users = List.of(
                user1,
                User.builder()
                        .id(2L)
                        .name("user2")
                        .email("user2@user.com")
                        .build()
        );
        Mockito.when(mockRepository.findAll()).thenReturn(users);
        assertEquals(users, userService.findAll());
        Mockito.verify(mockRepository, Mockito.times(1)).findAll();
    }

    @Test
    void updateAllFieldsTest() {
        User updatedUser = User.builder()
                .id(1L)
                .name("updated name")
                .email("updated@user.com")
                .build();
        assertEquals(updatedUser, userService.update(updatedUser));
        Mockito.verify(mockRepository, Mockito.times(1)).save(updatedUser);
    }

    @Test
    void updatePartialTest() {
        User userPartial = User.builder()
                .id(1L)
                .name("updated name")
                .build();
        User returnedUser = userService.update(userPartial);
        assertEquals(user1.getEmail(), returnedUser.getEmail());
        assertEquals(userPartial.getName(), returnedUser.getName());
        Mockito.verify(mockRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void updateNonExistingTest() {
        assertThrows(UserNotFoundException.class, () -> userService.update(User.builder().id(-1L).build()));
    }

    @Test
    void findNonExistingTest() {
        assertThrows(UserNotFoundException.class, () -> userService.findById(-1L));
    }

    @Test
    void deleteTest() {
        userService.deleteById(1L);
        Mockito.verify(mockRepository, Mockito.times(1)).deleteById(1L);
    }
}