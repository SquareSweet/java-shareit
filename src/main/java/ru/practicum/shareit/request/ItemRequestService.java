package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserService userService;

    public ItemRequest create(ItemRequest request, Long userId) {
        User requester = userService.findById(userId); //throws exception if user does not exist
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);
        log.info("Item request id: {} created", request.getId());
        return request;
    }

    public List<ItemRequest> findByUser(Long userId) {
        userService.findById(userId); //throws exception if user does not exist
        return requestRepository.findByRequesterIdOrderByCreatedDesc(userId);
    }

    public ItemRequest findById(Long id, Long userId) {
        userService.findById(userId); //throws exception if user does not exist
        return requestRepository.findById(id).orElseThrow(() -> new ItemRequestNotFound(id));
    }

    public List<ItemRequest> findAll(Long userId, int from, int size) {
        userService.findById(userId); //throws exception if user does not exist
        return requestRepository.findByRequesterIdNot(userId, PageRequest.of(from, size, Sort.by("created")));
    }
}
