package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.OffsetPageRequest;
import ru.practicum.shareit.common.exceptions.ItemNeverBookedByUserException;
import ru.practicum.shareit.common.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.common.exceptions.ItemNotFoundException;
import ru.practicum.shareit.common.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    public Item create(Item item, Long userId) {
        User owner = userService.findById(userId); //throws exception if user does not exist
        item.setOwner(owner);
        item.setAvailable(true);
        item = itemRepository.save(item);
        log.info("Create item id: {}", item.getId());
        return item;
    }

    public Item update(Item item, Long userId) {
        userService.findById(userId); //throws exception if user does not exist
        Item existingItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new ItemNotFoundException(item.getId()));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new UserIsNotOwnerException(userId, item.getId());
        }
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        log.info("Update item id: {}", item.getId());
        return itemRepository.save(existingItem);
    }

    public ItemDtoOut findById(Long id, Long userId) {
        ItemDtoOut itemOut = ItemMapper.toItemDtoOut(
                itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id))
        );
        fillBookings(itemOut, userId);
        fillComments(itemOut);
        return itemOut;
    }

    private ItemDtoOut fillBookings(ItemDtoOut item, Long userId) {
        if (item.getOwner().getId().equals(userId)) {
            List<Booking> itemBookings = bookingRepository.findByItemIdAndStatus(item.getId(), BookingStatus.APPROVED);
            Optional<Booking> nextBooking = itemBookings.stream()
                    .filter(i -> i.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStart));
            Optional<Booking> lastBooking = itemBookings.stream()
                    .filter(i -> i.getEnd().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getEnd));
            nextBooking.ifPresent(booking -> item.setNextBooking(BookingMapper.toBookingDtoShort(booking)));
            lastBooking.ifPresent(booking -> item.setLastBooking(BookingMapper.toBookingDtoShort(booking)));
        }
        return item;
    }

    public Item findForBookingById(Long id, Long bookerId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ItemNotFoundException(id);
        } else if (!item.getAvailable()) {
            throw new ItemNotAvailableException(id);
        } else {
            return item;
        }
    }

    public List<ItemDtoOut> findByOwner(Long userId, int from, int size) {
        return itemRepository.findByOwnerId(userId, OffsetPageRequest.of(from, size, Sort.by("id"))).stream()
                .map(ItemMapper::toItemDtoOut)
                .map(i -> fillBookings(i, userId))
                .map(i -> fillComments(i))
                .collect(Collectors.toList());
    }

    public List<Item> findByText(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByText(text, OffsetPageRequest.of(from, size, Sort.by("id")));
    }

    public Comment addComment(Long itemId, Long userId, Comment comment) {
        Item item = ItemMapper.toItem(findById(itemId, userId));
        User user = userService.findById(userId);
        if (bookingRepository.findByItemIdAndStatus(itemId, BookingStatus.APPROVED).stream()
                .filter(i -> i.getStart().isBefore(LocalDateTime.now()))
                .noneMatch(i -> i.getBooker().getId().equals(userId))) {
            throw new ItemNeverBookedByUserException(userId, itemId);
        }

        comment.setCreated(LocalDateTime.now().withNano(0));
        comment.setAuthor(user);
        comment.setItem(item);
        comment = commentRepository.save(comment);

        log.info("Create comment id: {} by user id: {}", comment.getId(), userId);
        return comment;
    }

    private ItemDtoOut fillComments(ItemDtoOut item) {
        item.setComments(commentRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return item;
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
        log.info("Delete item id: {}", id);
    }
}
