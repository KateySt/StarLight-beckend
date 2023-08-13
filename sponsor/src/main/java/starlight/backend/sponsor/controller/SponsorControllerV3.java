package starlight.backend.sponsor.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import starlight.backend.sponsor.service.SponsorServiceInterface;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SponsorControllerV3 {
    private SponsorServiceInterface sponsorService;

    @PostMapping("/sponsors/{sponsor-id}")
    public void getUnusableKudosForSponsor(@PathVariable("sponsor-id") long sponsorId,
                                           @RequestBody int kudosRequest) {
        log.info("@PostMapping(\"/sponsors/{sponsor-id}\")");
        sponsorService.setUnusableKudos(sponsorId, kudosRequest);
    }
    @GetMapping("/sponsor/{sponsor-id}")
    public boolean isSponsorExistedById(@PathVariable("sponsor-id") long sponsorId) {
        log.info("@GetMapping(\"/sponsor\")");
        return sponsorService.isSponsorExistedById(sponsorId);
    }
}
