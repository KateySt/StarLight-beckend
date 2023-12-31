package starlight.backend.sponsor;

import org.mapstruct.Mapper;
import starlight.backend.sponsor.model.entity.SponsorEntity;
import starlight.backend.sponsor.model.response.Sponsor;
import starlight.backend.sponsor.model.response.SponsorFullInfo;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface SponsorMapper {
    default SponsorFullInfo toSponsorFullInfo(SponsorEntity sponsor) {
        return SponsorFullInfo.builder()
                .fullName(sponsor.getFullName())
                .avatar(sponsor.getAvatar())
                .company(sponsor.getCompany())
                .unusedKudos(sponsor.getUnusedKudos())
                .build();
    }

    default Sponsor toSponsor(SponsorEntity sponsor) {
        return Sponsor.builder()
                .sponsorId(sponsor.getSponsorId())
                .email(sponsor.getEmail())
                .password(sponsor.getPassword())
                .build();
    }
}
