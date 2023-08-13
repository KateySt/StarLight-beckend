package starlight.backend.vacancy.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record NewVacancy(
        @NotBlank
        String title,
        @NotBlank
        String text,
        long skillId
) {
}
