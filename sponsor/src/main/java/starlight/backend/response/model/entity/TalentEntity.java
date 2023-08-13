package starlight.backend.response.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;
import starlight.backend.response.model.entity.ResponseEntity;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "talent")
public class TalentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long talentId;
    @NotBlank
    private String fullName;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    private LocalDate birthday;
    @URL
    private String avatar;
    @Length(max = 255)
    private String education;
    @Length(max = 255)
    private String experience;
    @OneToMany(mappedBy = "talent")
    @JsonManagedReference
    private Set<ResponseEntity> responses;
}
