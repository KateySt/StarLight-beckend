package starlight.backend.response.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.response.model.entity.ResponseEntity;

@Repository
public interface ResponseRepository extends JpaRepository<ResponseEntity, Long> {
    ResponseEntity findByTalent_TalentIdAndVacancy_VacancyId(Long talentId, Long vacancyId);
    boolean existsByTalent_TalentIdAndVacancy_VacancyId(Long talentId, Long vacancyId);
}