package starlight.backend.email.model;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties(prefix = "spring.mail")
public record EmailProps(
        String username,
        String password
) {
}
