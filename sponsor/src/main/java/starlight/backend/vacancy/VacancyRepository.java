package starlight.backend.vacancy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.vacancy.model.entity.VacancyEntity;

@Repository
public interface VacancyRepository extends JpaRepository<VacancyEntity, Long> {
    Page<VacancyEntity> findBySponsor_SponsorId(long sponsorId, Pageable pageable);
}