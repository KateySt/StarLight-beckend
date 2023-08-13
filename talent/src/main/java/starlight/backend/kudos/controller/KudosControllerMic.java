package starlight.backend.kudos.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import starlight.backend.kudos.model.response.KudosWithProofId;
import starlight.backend.kudos.service.KudosServiceInterface;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/api/v3")
@Validated
@Slf4j
@RestController
public class KudosControllerMic {
    private KudosServiceInterface kudosService;

    @GetMapping("/kudos/{sponsor-id}")
    public List<KudosWithProofId> getKudosOnProofForSponsor(@PathVariable("sponsor-id") long sponsorId) {
        log.info("@GetMapping(\"/kudos/{sponsor-id}\")");
        return kudosService.getKudosOnProofForSponsor(sponsorId);
    }
}
