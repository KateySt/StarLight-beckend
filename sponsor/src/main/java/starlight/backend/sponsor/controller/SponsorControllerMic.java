package starlight.backend.sponsor.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import starlight.backend.sponsor.model.request.NewUser;
import starlight.backend.sponsor.model.response.Sponsor;
import starlight.backend.sponsor.service.SponsorServiceInterface;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v3")
@Slf4j
public class SponsorControllerMic {
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
    @GetMapping("/sponsor")
    public Sponsor getSponsorByEmail(@RequestParam String email) {
        log.info("@GetMapping(\"/sponsor\")");
        return sponsorService.getSponsorByEmail(email);
    }

    @PostMapping("/sponsor")
    public Sponsor saveSponsor(@RequestBody NewUser newUser) {
        log.info("@PostMapping(\"/sponsor\")");
        return sponsorService.saveSponsor(newUser);
    }
}
