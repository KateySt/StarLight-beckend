package starlight.backend.sponsor.model.response;

import lombok.Builder;

@Builder
public record Sponsor(
        long sponsorId,
        String email,
        String password
) {
}
