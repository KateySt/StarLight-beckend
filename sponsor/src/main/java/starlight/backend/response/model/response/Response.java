package starlight.backend.response.model.response;

import lombok.Builder;
import starlight.backend.response.model.entity.TalentEntity;
import starlight.backend.vacancy.model.entity.VacancyEntity;

import java.time.Instant;

@Builder
public record Response(
        long responseId,
        TalentEntity talent,
        VacancyEntity vacancy,
        Instant responseDateCreate
) {
}
