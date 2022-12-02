package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating request {}, userId={}", itemRequestDto, userId);
        return requestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findRequestByRequester(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get requests for user {}", userId);
        return requestClient.findRequestByRequester(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestById(@PathVariable Long requestId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return requestClient.findRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Get all requests, userId={}, from={}, size={}", userId, from, size);
        return requestClient.findAllRequests(userId, from, size);
    }
}
