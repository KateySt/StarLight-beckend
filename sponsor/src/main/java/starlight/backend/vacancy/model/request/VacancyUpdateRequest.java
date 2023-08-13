package starlight.backend.vacancy.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record VacancyUpdateRequest(
        @NotBlank
        String title,
        @NotBlank
        String text,
        Long skillId
) {
}
