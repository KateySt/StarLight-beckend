package starlight.backend.response.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.response.model.entity.TalentEntity;

@Repository
public interface TalentRepository extends JpaRepository<TalentEntity, Long> {
}