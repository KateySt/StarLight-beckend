package starlight.backend.response.model.response;

import lombok.Builder;
import starlight.backend.vacancy.model.entity.VacancyEntity;

import java.time.Instant;

@Builder
public record Response(
        long responseId,
        long talentId,
        VacancyEntity vacancy,
        Instant responseDateCreate
) {
}
