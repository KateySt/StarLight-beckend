package starlight.backend.kudos.service;


import starlight.backend.kudos.model.entity.KudosEntity;
import starlight.backend.kudos.model.request.AddKudosOnProof;
import starlight.backend.kudos.model.response.KudosOnProof;
import starlight.backend.kudos.model.response.KudosWithProofId;

import java.util.List;

public interface KudosServiceInterface {
    KudosOnProof getKudosOnProof(long proofId);

    KudosEntity addKudosOnProof(long proofId, AddKudosOnProof addKudosOnProof);

    List<KudosWithProofId> getKudosOnProofForSponsor(long sponsorId);
}
