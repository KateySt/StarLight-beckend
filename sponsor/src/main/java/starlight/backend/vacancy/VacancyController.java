package starlight.backend.vacancy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import starlight.backend.vacancy.model.request.NewVacancy;
import starlight.backend.vacancy.model.request.VacancyUpdateRequest;
import starlight.backend.vacancy.model.response.VacancyFullInfo;
import starlight.backend.vacancy.model.response.VacancyPagePagination;
import starlight.backend.vacancy.service.VacancyServiceInterface;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Slf4j
@Tag(name = "Vacancy", description = "Vacancy related endpoints")
public class VacancyController {
    private VacancyServiceInterface vacancyService;

    @Operation(
            summary = "Create a new vacancy",
            description = "Create a new vacancy",
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
                                            implementation = VacancyFullInfo.class
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "409", description = "Conflict")
            }
    )
    @PostMapping("/vacancies")
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyFullInfo creatVacancy(@Valid @RequestBody NewVacancy newVacancy) {
        log.info("@PostMapping(\"/vacancies\")");
        return vacancyService.creatVacancy(newVacancy);
    }

    @Operation(
            summary = "Get all vacancies",
            description = "Get list of all vacancies."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = VacancyPagePagination.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = Exception.class
                            )
                    )
            )
    })
    @GetMapping("/vacancies")
    public VacancyPagePagination pagination(@RequestParam(defaultValue = "0") @Min(0) int page,
                                            @RequestParam(defaultValue = "10") @Positive int size) {

        log.info("@GetMapping(\"/vacancies\")");
        return vacancyService.vacancyPagination(page, size);
    }

    @Operation(
            summary = "Get all vacancies from sponsor",
            description = "Get list of all vacancies."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = VacancyPagePagination.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = Exception.class
                            )
                    )
            )
    })
    @GetMapping("/sponsors/{sponsor-id}/vacancies")
    public VacancyPagePagination paginationVacancyFromSponsor(@PathVariable("sponsor-id") long sponsorId,
                                                              @RequestParam(defaultValue = "0") @Min(0) int page,
                                                              @RequestParam(defaultValue = "10") @Positive int size) {

        log.info("@GetMapping(\"/sponsors/{sponsor-id}/vacancies\")");
        return vacancyService.vacancyPaginationFromSponsor(page, size, sponsorId);
    }

    @Operation(
            summary = "Get vacancy by id",
            description = "Get a vacancy from id.")
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
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/vacancies/{vacancy-id}")
    public VacancyFullInfo searchVacancyById(@PathVariable("vacancy-id") long vacancyId) {
        log.info("@GetMapping(\"/vacancies/{vacancy-id}\")");
        return vacancyService.vacancyFullInfo(vacancyId);
    }

    @Operation(
            summary = "Update vacancy by id",
            description = "Update a vacancy from id.")
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
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    @PatchMapping("/vacancies/{vacancy-id}")
    public VacancyFullInfo updateVacancyFullInfo(@PathVariable("vacancy-id") long vacancyId,
                                                 @RequestBody VacancyUpdateRequest vacancyUpdateRequest) {
        log.info("@PatchMapping(\"/talents/{talent-id}\")");
        return vacancyService.updateVacancyProfile(vacancyId, vacancyUpdateRequest);
    }

    @Operation(summary = "Delete vacancy by id")
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
    @DeleteMapping("/vacancies/{vacancy-id}")
    public void deleteVacancy(@PathVariable("vacancy-id") long vacancyId) {
        log.info("@DeleteMapping(\"/vacancies/{vacancy-id}\")");
        vacancyService.deleteVacancyProfile(vacancyId);
    }
}
