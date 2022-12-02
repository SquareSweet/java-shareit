package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserDto create(@RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.create(user));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId);
        return UserMapper.toUserDto(userService.update(user));
    }

    @GetMapping("/{userId}")
    public UserDto find(@PathVariable Long userId) {
        return UserMapper.toUserDto(userService.findById(userId));
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.deleteById(userId);
    }
}
