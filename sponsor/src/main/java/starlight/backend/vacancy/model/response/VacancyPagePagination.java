package starlight.backend.vacancy.model.response;

import lombok.Builder;

import java.util.List;

@Builder
public record VacancyPagePagination(
        long total,
        List<Vacancy> data
) {
}
