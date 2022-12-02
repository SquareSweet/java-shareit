package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.common.OffsetPageRequest;
import ru.practicum.shareit.common.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestServiceTest {
    private User user;
    private ItemRequest request;

    @Autowired
    private ItemRequestService requestService;

    @MockBean
    private UserService mockUserService;

    @MockBean
    private ItemRequestRepository mockRepository;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
        request = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .description("description")
                .created(LocalDateTime.now().withNano(0))
                .build();
        Mockito.when(mockUserService.findById(1L)).thenReturn(user);
        Mockito.when(mockRepository.findById(1L)).thenReturn(Optional.of(request));
    }

    @Test
    void createTest() {
        Mockito.when(mockRepository.save(request)).thenReturn(request);
        assertEquals(request, requestService.create(request, 1L));
        Mockito.verify(mockRepository, Mockito.times(1)).save(request);
    }

    @Test
    void findByIdTest() {
        assertEquals(request, requestService.findById(1L, 1L));
        Mockito.verify(mockRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void findByIdNotFoundTest() {
        assertThrows(ItemRequestNotFound.class, () -> requestService.findById(2L, 1L));
    }

    @Test
    void findByUserTest() {
        Mockito.when(mockRepository.findByRequesterIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        assertEquals(List.of(request), requestService.findByUser(1L));
        Mockito.verify(mockRepository, Mockito.times(1)).findByRequesterIdOrderByCreatedDesc(1L);
    }

    @Test
    void findAllTest() {
        Mockito.when(mockRepository.findByRequesterIdNot(Mockito.anyLong(), Mockito.any(OffsetPageRequest.class)))
                .thenReturn(List.of(request));
        assertEquals(List.of(request), requestService.findAll(1L, 0, 20));
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByRequesterIdNot(Mockito.anyLong(), Mockito.any(OffsetPageRequest.class));
    }
}