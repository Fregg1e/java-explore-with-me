package ru.practicum.ewm.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class UpdateCompilationRequest {
    private List<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;

    @AssertTrue(message = "Хотя бы одно поле должно быть не пустым.")
    private boolean isAllFieldsNotNull() {
        return events != null || pinned != null || title != null;
    }
}
