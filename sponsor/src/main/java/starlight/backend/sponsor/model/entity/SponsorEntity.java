package starlight.backend.sponsor.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;
import starlight.backend.sponsor.model.enums.SponsorStatus;
import starlight.backend.vacancy.model.entity.VacancyEntity;

import java.time.Instant;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "sponsor")
public class SponsorEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long sponsorId;
    @NotBlank
    private String fullName;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @URL
    private String avatar;
    private String company;
    private Integer unusedKudos;
    private String activationCode;
    private Instant expiryDate;
    @Enumerated(EnumType.STRING)
    private SponsorStatus status;

    @OneToMany(mappedBy = "sponsor")
    private Set<VacancyEntity> vacancies;
}
