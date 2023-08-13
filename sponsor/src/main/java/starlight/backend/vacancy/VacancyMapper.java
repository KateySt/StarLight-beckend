package starlight.backend.vacancy;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import starlight.backend.vacancy.model.entity.VacancyEntity;
import starlight.backend.vacancy.model.request.NewVacancy;
import starlight.backend.vacancy.model.response.SkillWithCategory;
import starlight.backend.vacancy.model.response.Vacancy;
import starlight.backend.vacancy.model.response.VacancyFullInfo;
import starlight.backend.vacancy.model.response.VacancyPagePagination;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface VacancyMapper {
    default VacancyEntity toVacancyEntity(NewVacancy newVacancy) {
        return VacancyEntity.builder()
                .text(newVacancy.text())
                .title(newVacancy.title())
                .skillId(newVacancy.skillId())
                .build();
    }

    default Vacancy toVacancy(VacancyEntity vacancyEntity) {
        return Vacancy.builder()
                .vacancyId(vacancyEntity.getVacancyId())
                .dataCreate(vacancyEntity.getDataCreate())
                .sponsorId(vacancyEntity.getSponsor().getSponsorId())
                .text(vacancyEntity.getText())
                .title(vacancyEntity.getTitle())
                .build();
    }

    default VacancyFullInfo toVacancyFullInfo(VacancyEntity vacancyEntity, SkillWithCategory skill){
        return VacancyFullInfo.builder()
                .vacancyId(vacancyEntity.getVacancyId())
                .dataCreate(vacancyEntity.getDataCreate())
                .sponsorId(vacancyEntity.getSponsor().getSponsorId())
                .text(vacancyEntity.getText())
                .title(vacancyEntity.getTitle())
                .skill(skill)
                .build();
    }

   default VacancyPagePagination toVacancyPagePagination(Page<VacancyEntity> pageRequest){
        return VacancyPagePagination.builder()
                .data(pageRequest.getContent().
                        stream().map(this::toVacancy).toList())
                .total(pageRequest.getTotalElements())
                .build();
   }
}
