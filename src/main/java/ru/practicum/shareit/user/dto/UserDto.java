package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;

    @Email(message = "Email should have valid email format")
    @NotBlank(message = "Email should not be blank")
    private String email;

    @NotBlank(message = "User name should not be blank")
    private String name;
}
