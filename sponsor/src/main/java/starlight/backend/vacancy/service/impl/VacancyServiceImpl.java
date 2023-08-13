package starlight.backend.vacancy.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import starlight.backend.exception.PageNotFoundException;
import starlight.backend.exception.vacancy.VacancyNotFoundException;
import starlight.backend.vacancy.VacancyMapper;
import starlight.backend.vacancy.VacancyRepository;
import starlight.backend.vacancy.model.request.NewVacancy;
import starlight.backend.vacancy.model.request.VacancyUpdateRequest;
import starlight.backend.vacancy.model.response.SkillWithCategory;
import starlight.backend.vacancy.model.response.VacancyFullInfo;
import starlight.backend.vacancy.model.response.VacancyPagePagination;
import starlight.backend.vacancy.service.VacancyServiceInterface;

import java.time.Instant;
import java.util.Objects;

@Service
@AllArgsConstructor
public class VacancyServiceImpl implements VacancyServiceInterface {
    private VacancyRepository vacancyRepository;
    private VacancyMapper mapper;
    private final String filterParam = "dataCreate";
    private RestTemplate restTemplate;

    @Override
    public VacancyFullInfo creatVacancy(NewVacancy newVacancy) {
        SkillWithCategory skill = restTemplate.getForObject(
                "http://TALENT/api/v3/skill/" + newVacancy.skillId(),
                SkillWithCategory.class
        );
        var vacancy = vacancyRepository.save(mapper.toVacancyEntity(newVacancy));
        return mapper.toVacancyFullInfo(vacancy, skill);
    }

    @Override
    public VacancyPagePagination vacancyPagination(int page, int size) {
        var pageRequest = vacancyRepository.findAll(
                PageRequest.of(page, size, Sort.by(filterParam).descending())
        );
        if (page >= pageRequest.getTotalPages())
            throw new PageNotFoundException(page);
        return mapper.toVacancyPagePagination(pageRequest);
    }


    @Override
    public VacancyPagePagination vacancyPaginationFromSponsor(int page, int size, long sponsorId) {
        var pageRequest = vacancyRepository.findBySponsor_SponsorId(
                sponsorId,
                PageRequest.of(page, size, Sort.by(filterParam).descending())
        );
        if (page >= pageRequest.getTotalPages())
            throw new PageNotFoundException(page);
        return mapper.toVacancyPagePagination(pageRequest);
    }

    @Override
    public VacancyFullInfo vacancyFullInfo(long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .map(el -> {
                    SkillWithCategory skill = restTemplate.getForObject(
                            "http://TALENT/api/v3/skill/" + el.getSkillId(),
                            SkillWithCategory.class
                    );
                    return mapper.toVacancyFullInfo(el, skill);
                })
                .orElseThrow(() -> new VacancyNotFoundException(vacancyId));
    }

    @Override
    public VacancyFullInfo updateVacancyProfile(long vacancyId, VacancyUpdateRequest vacancyUpdateRequest) {
        return vacancyRepository.findById(vacancyId).map(vacancy -> {
                    vacancy.setTitle(validationField(
                            vacancyUpdateRequest.title(),
                            vacancy.getTitle()));
                    vacancy.setText(validationField(
                            vacancyUpdateRequest.text(),
                            vacancy.getText()));
                    vacancy.setDataCreate(Instant.now());
                    SkillWithCategory skill = restTemplate.getForObject(
                            "http://TALENT/api/v3/skill/" + vacancy.getSkillId(),
                            SkillWithCategory.class
                    );
                    vacancy.setSkillId(vacancyUpdateRequest.skillId() == null ?
                            Objects.requireNonNull(skill).skillId() : vacancyUpdateRequest.skillId());
                    vacancyRepository.save(vacancy);
                    return mapper.toVacancyFullInfo(vacancy, skill);
                })
                .orElseThrow(() -> new VacancyNotFoundException(vacancyId));
    }

    private String validationField(String newParam, String lastParam) {
        return newParam == null ?
                lastParam :
                newParam;
    }

    @Override
    public void deleteVacancyProfile(long vacancyId) {
        var vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new VacancyNotFoundException(vacancyId));
        vacancy.setSponsor(null);
        vacancyRepository.delete(vacancy);
    }
}
