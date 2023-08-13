package starlight.backend.sponsor.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import starlight.backend.advice.config.AdviceConfiguration;
import starlight.backend.advice.model.entity.DelayedDeleteEntity;
import starlight.backend.advice.model.enums.DeletingEntityType;
import starlight.backend.advice.repository.DelayDeleteRepository;
import starlight.backend.advice.service.AdviceService;
import starlight.backend.email.model.EmailProps;
import starlight.backend.email.service.EmailService;
import starlight.backend.exception.EmailAlreadyOccupiedException;
import starlight.backend.exception.user.sponsor.SponsorAlreadyOnDeleteList;
import starlight.backend.exception.user.sponsor.SponsorNotFoundException;
import starlight.backend.sponsor.SponsorMapper;
import starlight.backend.sponsor.SponsorRepository;
import starlight.backend.sponsor.model.entity.SponsorEntity;
import starlight.backend.sponsor.model.enums.SponsorStatus;
import starlight.backend.sponsor.model.request.NewUser;
import starlight.backend.sponsor.model.request.SponsorUpdateRequest;
import starlight.backend.sponsor.model.response.KudosWithProofId;
import starlight.backend.sponsor.model.response.Sponsor;
import starlight.backend.sponsor.model.response.SponsorFullInfo;
import starlight.backend.sponsor.model.response.SponsorKudosInfo;
import starlight.backend.sponsor.service.SponsorServiceInterface;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
@Transactional
@Slf4j
public class SponsorServiceImpl implements SponsorServiceInterface {
    private EmailProps emailProps;
    private SponsorRepository sponsorRepository;
    private DelayDeleteRepository delayDeleteRepository;
    private AdviceConfiguration adviceConfiguration;
    private SponsorMapper sponsorMapper;
    private EmailService emailService;
    private AdviceService adviceService;
    private RestTemplate restTemplate;


    @Override
    public SponsorKudosInfo getUnusableKudos(long sponsorId) {//TODo проверить работает лт
        var sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new SponsorNotFoundException(sponsorId));
        ParameterizedTypeReference<List<KudosWithProofId>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<KudosWithProofId>> responseEntity = restTemplate.exchange(
                "http://TALENT/api/v3/kudos/" + sponsorId,
                HttpMethod.GET,
                null,
                responseType
        );

        List<KudosWithProofId> kudosList = responseEntity.getBody();

        int alreadyMarkedKudos = kudosList.stream()
                .map(KudosWithProofId::countKudos)
                .reduce(Integer::sum)
                .orElse(0);
        log.info("alreadyMarkedKudos{}", alreadyMarkedKudos);
        return new SponsorKudosInfo(sponsor.getUnusedKudos(), alreadyMarkedKudos, kudosList);
    }

    @Override
    public SponsorFullInfo getSponsorFullInfo(long sponsorId) {
        var sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new SponsorNotFoundException(sponsorId));
        return SponsorFullInfo.builder()
                .fullName(sponsor.getFullName())
                .avatar(sponsor.getAvatar())
                .company(sponsor.getCompany())
                .unusedKudos(sponsor.getUnusedKudos())
                .build();
    }

    @Override
    public SponsorFullInfo updateSponsorProfile(long sponsorId, SponsorUpdateRequest sponsorUpdateRequest) {
        return sponsorRepository.findById(sponsorId).map(sponsor -> {
                    sponsor.setAvatar(validationField(
                            sponsorUpdateRequest.avatar(),
                            sponsor.getAvatar()));
                    sponsor.setCompany(validationField(
                            sponsorUpdateRequest.company(),
                            sponsor.getCompany()));
                    sponsor.setFullName(validationField(
                            sponsorUpdateRequest.fullName(),
                            sponsor.getFullName()));
                    sponsor.setUnusedKudos(addKudos(sponsor, sponsorUpdateRequest.unusedKudos()));
                    sponsorRepository.save(sponsor);
                    return sponsorMapper.toSponsorFullInfo(sponsor);
                })
                .orElseThrow(() -> new SponsorNotFoundException(sponsorId));
    }

    private int addKudos(SponsorEntity sponsor, int unusedKudos) {
        if (unusedKudos <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you can't reduce the number of kudos");
        }
        var countKudos = unusedKudos + sponsor.getUnusedKudos();
        if (countKudos >= 100000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you can't add more 100 000 kudos");
        }
        sponsor.setUnusedKudos(countKudos);
        return sponsor.getUnusedKudos();
    }

    private String validationField(String newParam, String lastParam) {
        return newParam == null ?
                lastParam :
                newParam;
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteSponsor(long sponsorId) {
        if (sponsorRepository.existsBySponsorId(sponsorId)) {
            throw new SponsorNotFoundException(sponsorId);
        }
        sponsorRepository.findById(sponsorId).ifPresent(sponsor -> {
            if (delayDeleteRepository.existsByEntityId(sponsor.getSponsorId())) {
                throw new SponsorAlreadyOnDeleteList(sponsor.getSponsorId());
            }
            delayDeleteRepository.save(
                    DelayedDeleteEntity.builder()
                            .entityId(sponsor.getSponsorId())
                            .deletingEntityType(DeletingEntityType.SPONSOR)
                            .deleteDate(Instant.now().plus(adviceConfiguration.delayDays(), ChronoUnit.DAYS))
                            .userDeletingProcessUuid(UUID.randomUUID())
                            .build()
            );
            sponsor.setStatus(SponsorStatus.DELETING);
            sponsorRepository.save(sponsor);
        });
        return ResponseEntity.ok(
                "Dear sponsor,\n" +
                        "We are sorry to see you go.\n" +
                        "Your sponsor profile has been deleted after 7 days!\n" +
                        "If you want to restore your account, " +
                        "please sign in and send recovery request.\n" +
                        "Thank you for your support\n" +
                        "If you have any questions, please contact us at:\n" +
                        emailProps.username() + "\n" +
                        "We are looking forward to hearing from you.\n" +
                        "Best regards,\n" +
                        "Starlight Team"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public String getSponsorMail(long sponsorId) {
        if (sponsorRepository.existsBySponsorId(sponsorId)) {
            throw new SponsorNotFoundException(sponsorId);
        }
        return sponsorRepository.findById(sponsorId)
                .map(SponsorEntity::getEmail)
                .orElseThrow(() -> new SponsorNotFoundException(sponsorId));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<String> sendEmailForRecoverySponsorAccount(long sponsorId) {
        String email = getSponsorMail(sponsorId);
        emailService.sendRecoveryMessageSponsorAccount(email, adviceService.getUUID(sponsorId));
        return ResponseEntity.ok("Email sent to " + email);

    }

    @Override
    public void setUnusableKudos(long sponsorId, int kudosRequest) {
        var sponsor = sponsorRepository.findById(sponsorId).orElseThrow(() -> new SponsorNotFoundException(sponsorId));
        sponsor.setUnusedKudos(kudosRequest);
        sponsorRepository.save(sponsor);
    }

    @Override
    public boolean isSponsorExistedById(long sponsorId) {
        return sponsorRepository.existsBySponsorId(sponsorId);
    }

    @Override
    public Sponsor getSponsorByEmail(String email) {
        if (sponsorRepository.existsByEmail(email)) {
            throw new SponsorNotFoundException(email);
        }
        var sponsor = sponsorRepository.findByEmail(email);
        return sponsorMapper.toSponsor(sponsor);
    }

    @Override
    public Sponsor saveSponsor(NewUser user) {
        if (sponsorRepository.existsByEmail(user.email())) {
            throw new EmailAlreadyOccupiedException(user.email());
        }
        var sponsor = sponsorRepository.save(SponsorEntity.builder()
                .email(user.email())
                .password(user.password())
                .fullName(user.fullName())
                .build());
        return sponsorMapper.toSponsor(sponsor);
    }
}
