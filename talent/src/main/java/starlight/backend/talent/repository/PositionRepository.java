package starlight.backend.talent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.talent.model.entity.PositionEntity;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, Long> {
    boolean existsByPositionIdAndUsers_TalentId(Long positionId, Long talentId);
    boolean existsByPositionIgnoreCase(String position);

    PositionEntity findByPosition(String position);
}