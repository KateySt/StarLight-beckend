package starlight.backend.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public record S3Props (
        @Value("${s3.bucket}")
        String bucket,
        @Value("${s3.access-key}")
        String accessKey,
        @Value("${s3.secret-key}")
        String secretKey,
        @Value("${s3.region}")
        String region
){
    @PostConstruct
    void logLoaded() {
        log.info("props = {}", this);
    }
}