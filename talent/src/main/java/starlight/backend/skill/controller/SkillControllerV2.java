package starlight.backend.skill.controller;

/*
@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/v2")
@Tag(name = "Skill v2", description = "Skill API v2")
public class SkillControllerV2 {
    private SkillServiceInterface serviceService;

    @Operation(
            summary = "Add skill to Talent",
            description = "Add a Skill to a Talent, given the Talent ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TalentWithSkills.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasRole('TALENT') or hasRole('ADMIN')")
    @PostMapping("talents/{talent-id}/skills")
    public TalentWithSkills addSkillToTalent(@PathVariable("talent-id") long talentId,
                                             @RequestBody AddSkill skills,
                                             Authentication auth) {
        log.info("@PostMapping(\"talents/{talent-id}/skills\")");
        return serviceService.addSkillToTalent(talentId, skills, auth);
    }

    @Operation(
            summary = "Add skill",
            description = "Add a Skill to a Proof, given the Proof ID and the Talent ID, only if the Proof is in " +
                    "status \"Draft\" and the Talent owns the Proof. If Talent doesn't have this Skill add this Skill to Talent"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProofWithSkills.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('TALENT') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/talents/{talent-id}/proofs/{proof-id}/skills")
    public ProofWithSkills addSkillInProof(@PathVariable("talent-id") long talentId,
                                           @PathVariable("proof-id") long proofId,
                                           @RequestBody AddSkill skills,
                                           Authentication auth) {
        log.info("@GetMapping(\"/talents/{talent-id}/proofs/{proof-id}/skills\")");
        return serviceService.addSkillInYourProofV2(talentId, proofId, auth, skills);
    }
}
*/