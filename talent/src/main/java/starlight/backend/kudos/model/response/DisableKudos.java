package starlight.backend.kudos.model.response;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties(prefix = "disable-kudos")
public record DisableKudos(
        int count
) {
}

