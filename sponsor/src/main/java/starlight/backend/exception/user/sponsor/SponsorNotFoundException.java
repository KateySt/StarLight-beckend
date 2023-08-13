package starlight.backend.exception.user.sponsor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SponsorNotFoundException extends RuntimeException {
    public SponsorNotFoundException(long id) {
        super("Sponsor not found by id " + id);
        log.info("Sponsor not found by id + {}", id);
    }
    public SponsorNotFoundException(String email) {
        super("Sponsor not found by email " + email);
        log.info("Sponsor not found by email + {}", email);
    }
}
