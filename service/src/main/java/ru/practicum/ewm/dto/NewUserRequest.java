package ru.practicum.ewm.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class NewUserRequest {
    @NotBlank(message = "Email не может быть пустым.")
    @Email(regexp = "^.+@.+$",
            message = "Email не соответствует должному формату.")
    @Size(min = 6, max = 254, message = "Email не соответствует должной длине.")
    private String email;
    @NotBlank(message = "Name не может быть пустым.")
    @Size(min = 2, max = 250, message = "Name не соответствует должной длине.")
    private String name;
}