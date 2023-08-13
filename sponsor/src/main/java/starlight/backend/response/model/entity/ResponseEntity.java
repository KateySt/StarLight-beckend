package starlight.backend.response.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import starlight.backend.vacancy.model.entity.VacancyEntity;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "response")
public class ResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responseId;

    private Long talentId;

    @ManyToOne
    @JoinColumn(name = "vacancy_id")
    private VacancyEntity vacancy;

    private Instant responseDateCreate;
}