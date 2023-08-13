package starlight.backend.response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import starlight.backend.response.model.response.Response;
import starlight.backend.response.service.ResponseServiceInterface;
import starlight.backend.vacancy.model.response.VacancyFullInfo;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Slf4j
@Tag(name = "Response", description = "Response related endpoints")
public class ResponseController {
    private ResponseServiceInterface responseService;

    @Operation(
            summary = "Create a new response",
            description = "Create a new response",
            tags = {"Security"}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = Response.class
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "409", description = "Conflict")
            }
    )
    @PostMapping("/vacancies/{vacancy-id}/talents/{talent-id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Response creatResponse(@PathVariable("talent-id") long talentId,
                                  @PathVariable("vacancy-id") long vacancyId) {
        log.info("@PostMapping(\"/vacancies/{vacancy-id}/talents/{talent-id}\")");
        return responseService.creatResponse(talentId, vacancyId);
    }

    @Operation(summary = "Delete response by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = VacancyFullInfo.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    @DeleteMapping("/vacancies/{vacancy-id}/talents/{talent-id}")
    public void deleteResponse(@PathVariable("talent-id") long talentId,
                               @PathVariable("vacancy-id") long vacancyId) {
        log.info("@DeleteMapping(\"/vacancies/{vacancy-id}/talents/{talent-id}\")");
        responseService.deleteResponse(talentId, vacancyId);
    }
}
