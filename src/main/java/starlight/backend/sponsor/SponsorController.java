package starlight.backend.sponsor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import starlight.backend.security.model.request.NewUser;
import starlight.backend.security.model.response.SessionInfo;
import starlight.backend.security.service.SecurityServiceInterface;
import starlight.backend.sponsor.model.response.SponsorFullInfo;
import starlight.backend.sponsor.model.response.UnusableKudos;
import starlight.backend.sponsor.service.SponsorServiceInterface;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Slf4j
@Tag(name = "Sponsor", description = "Sponsor related endpoints")
public class SponsorController {
    private SponsorServiceInterface sponsorService;

    @Operation(
            summary = "Get unusable Sponsor's kudos",
            description = "Get unusable Sponsor's kudos"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SessionInfo.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SessionInfo.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SessionInfo.class)
                            )
                    )
            }
    )
    @GetMapping("/sponsors/{sponsor-id}/kudos")
    public UnusableKudos register(@PathVariable("sponsor-id") long sponsorId) {

        log.info("@GetMapping(\"/sponsors/{sponsor-id}/kudos\")");

        return sponsorService.getUnusableKudos(sponsorId);
    }

    @Operation(
            summary = "Get Sponsor full info",
            description = "Get Sponsor full info"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Exception.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Exception.class)
                            )
                    )
            }
    )
    @GetMapping("/sponsors/{sponsor-id}")
    @PreAuthorize("hasRole('SPONSOR')")
    public SponsorFullInfo sponsorFullInfo(@PathVariable("sponsor-id") long sponsorId,
                                           Authentication auth) {

        log.info("@GetMapping(\"/sponsors/{sponsor-id}\")");

        return sponsorService.getSponsorFullInfo(sponsorId, auth);
    }

    @Operation(
            summary = "Delete sponsor",
            description = "Deletes a sponsor by the specified identifier. Only users with the 'ROLE_SPONSOR' role can use this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful sponsor deletion"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Sponsor not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PreAuthorize("hasRole('ROLE_SPONSOR')")
    @PostMapping ("/sponsors/{sponsor-id}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("sponsor-id") long sponsorId,
                       Authentication auth,
                       HttpServletRequest request
    ) {

        log.info("@PostMapping(\"/sponsors/{sponsor-id}/delete\")");
        sponsorService.deleteSponsor(sponsorId, auth, request);
    }
}
