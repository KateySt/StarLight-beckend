package starlight.backend.vacancy.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.Instant;

@Builder
public record VacancyFullInfo(
        long vacancyId,
        String title,
        String text,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant dataCreate,
        long sponsorId,
        SkillWithCategory skill
) {
}
