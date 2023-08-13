package starlight.backend.user.model.response;

import lombok.Builder;

@Builder
public record Sponsor(
        long sponsor_id,
        String email,
        String password
) {
}
