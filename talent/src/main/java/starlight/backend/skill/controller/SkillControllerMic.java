package starlight.backend.skill.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import starlight.backend.skill.model.response.SkillWithCategory;
import starlight.backend.skill.service.SkillServiceInterface;

@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/v3")
public class SkillControllerMic {
    private SkillServiceInterface serviceService;

    @GetMapping("/skill/{skill-id}")
    public SkillWithCategory getSkillById(@PathVariable("skill-id") long skillId) {
        log.info("@GetMapping(\"/talent\")");
        return serviceService.getSkillById(skillId);
    }
}
