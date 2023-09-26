package ru.practicum.ewm.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class NewCategoryDto {
    @NotBlank(message = "Name не может быть пустым.")
    @Size(min = 1, max = 50, message = "Name не соответствует должной длине.")
    private String name;
}
