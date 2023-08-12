package starlight.backend.kudos.model.request;

import lombok.Builder;

@Builder
public record AddKudosOnProof(
        int kudos,
        long sponsorId
) {
}
