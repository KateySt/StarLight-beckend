package starlight.backend.response.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.response.model.entity.ResponseEntity;

@Repository
public interface ResponseRepository extends JpaRepository<ResponseEntity, Long> {
    boolean existsByTalentIdAndVacancy_VacancyId(Long talentId, Long vacancyId);

    ResponseEntity findByTalentIdAndVacancy_VacancyId(long talentId, long vacancyId);
}