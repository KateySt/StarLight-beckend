package starlight.backend.sponsor.model.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record Kudos(
        long kudosId,
        long followerId,
        int countKudos,
        Instant updateData,
        Instant createData,
        long proofId
) {
}
