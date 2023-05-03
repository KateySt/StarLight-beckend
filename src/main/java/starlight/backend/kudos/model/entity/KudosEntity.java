package starlight.backend.kudos.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import starlight.backend.proof.model.entity.ProofEntity;
import starlight.backend.user.model.entity.SponsorEntity;
import starlight.backend.user.model.entity.UserEntity;

import java.time.Instant;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class KudosEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long kudosId;
    private Long followerId;
    private Instant createData;
    private Integer countKudos;
    private Instant updateData;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "sponsor_id", nullable = false)
    private SponsorEntity owner;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "proof_id", nullable = false)
    private ProofEntity proof;
}
