package starlight.backend.kudos;

import org.mapstruct.Mapper;
import starlight.backend.kudos.model.entity.KudosEntity;
import starlight.backend.kudos.model.response.KudosWithProofId;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface KudosMapper {
    default KudosWithProofId toKudosWithProofId(KudosEntity kudos) {
        return KudosWithProofId.builder()
                .kudosId(kudos.getKudosId())
                .proofId(kudos.getProof().getProofId())
                .talentId(kudos.getFollowerId())
                .countKudos(kudos.getCountKudos())
                .updateData(kudos.getUpdateData())
                .createData(kudos.getCreateData())
                .build();
    }
}
