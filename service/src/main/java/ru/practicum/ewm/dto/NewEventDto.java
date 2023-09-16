package ru.practicum.ewm.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
public class NewEventDto {
    @NotBlank(message = "Аннотация не может быть пустой.")
    @Size(min = 20, max = 2000, message = "Аннотация не соответствует размерам.")
    private String annotation;
    @NotNull
    @Positive(message = "Id категории должно быть больше 0.")
    private Long category;
    @NotBlank(message = "Описание не может быть пустым.")
    @Size(min = 20, max = 7000, message = "Описание не соответствует размерам.")
    private String description;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero(message = "participantLimit категории должно быть 0 или больше 0.")
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotBlank(message = "Заголовок события не может быть пустым.")
    @Size(min = 3, max = 120, message = "Заголовок события не соответствует размерам.")
    private String title;
}
