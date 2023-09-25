package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000, message = "Аннотация не соответствует размерам.")
    private String annotation;
    @Positive(message = "Id категории должно быть больше 0.")
    private Long category;
    @Size(min = 20, max = 7000, message = "Описание не соответствует размерам.")
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero(message = "participantLimit категории должно быть 0 или больше 0.")
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventStateUserUpdate stateAction;
    @Size(min = 3, max = 120, message = "Заголовок события не соответствует размерам.")
    private String title;

    @AssertTrue(message = "Хотя бы одно поле должно быть не пустым.")
    private boolean isAllFieldsNotNull() {
        return annotation != null || category != null || description != null
                || eventDate != null || location != null || paid != null || participantLimit != null
                || requestModeration != null || stateAction != null || title != null;
    }
}
