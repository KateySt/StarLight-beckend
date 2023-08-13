package starlight.backend.vacancy.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import starlight.backend.sponsor.model.entity.SponsorEntity;

import java.time.Instant;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "vacancy")
public class VacancyEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long vacancyId;
    @NotBlank
    private String title;
    @NotBlank
    private String text;

    private Instant dataCreate;

    private Long skillId;
    @ManyToOne
    @JoinColumn(name = "sponsor_id", nullable = false)
    private SponsorEntity sponsor;
}
