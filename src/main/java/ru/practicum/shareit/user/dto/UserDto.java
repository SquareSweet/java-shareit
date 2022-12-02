package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;

    @Email(message = "Email should have valid email format")
    @NotBlank(message = "Email should not be blank")
    String email;

    @NotBlank(message = "User name should not be blank")
    String name;
}
