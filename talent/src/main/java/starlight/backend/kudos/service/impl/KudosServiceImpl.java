package starlight.backend.kudos.service.impl;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import starlight.backend.exception.kudos.KudosRequestMustBeNotZeroException;
import starlight.backend.exception.kudos.NotEnoughKudosException;
import starlight.backend.exception.kudos.YouCanNotReturnMoreKudosThanGaveException;
import starlight.backend.exception.proof.ProofNotFoundException;
import starlight.backend.exception.user.UserNotFoundException;
import starlight.backend.kudos.KudosMapper;
import starlight.backend.kudos.model.entity.KudosEntity;
import starlight.backend.kudos.model.request.AddKudosOnProof;
import starlight.backend.kudos.model.response.KudosOnProof;
import starlight.backend.kudos.model.response.KudosWithProofId;
import starlight.backend.kudos.repository.KudosRepository;
import starlight.backend.kudos.service.KudosServiceInterface;
import starlight.backend.proof.ProofRepository;
import starlight.backend.proof.model.entity.ProofEntity;
import starlight.backend.talent.model.entity.TalentEntity;
import starlight.backend.talent.repository.TalentRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class KudosServiceImpl implements KudosServiceInterface {
    private KudosRepository kudosRepository;
    private ProofRepository proofRepository;
    private TalentRepository talentRepository;
    private KudosMapper kudosMapper;
    private RestTemplate restTemplate;

    @Override
    @Transactional(readOnly = true)
    public KudosOnProof getKudosOnProof(long proofId) {
        var proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));
        var kudos = proof.getKudos();
        int countKudos = kudos
                .stream()
                .mapToInt(KudosEntity::getCountKudos)
                .sum();
        return KudosOnProof.builder()
                .kudosOnProof(countKudos)
                .build();

    }

    @Override
    public KudosEntity addKudosOnProof(long proofId, AddKudosOnProof addKudosOnProof) {
        if (addKudosOnProof.kudos() == 0) {
            throw new KudosRequestMustBeNotZeroException();
        }
        var proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));

        if (addKudosOnProof.kudos() > addKudosOnProof.sponsorId()) {//TODO .getUnusedKudos()) {
            throw new NotEnoughKudosException();
        }
        var talent = talentRepository.findById(proof.getTalent().getTalentId())
                .orElseThrow(() -> new UserNotFoundException(proof.getTalent().getTalentId()));
        updateSponsorUnusedKudos(addKudosOnProof.sponsorId(), addKudosOnProof.kudos());
        return updateSponsorKudosField(proof, talent, addKudosOnProof.sponsorId(), addKudosOnProof.kudos(), proofId);
    }

    @Override
    public List<KudosWithProofId> getKudosOnProofForSponsor(long sponsorId) {
        return kudosRepository.findBySponsorId(sponsorId)
                .stream()
                .map(el -> kudosMapper.toKudosWithProofId(el))
                .toList();
    }

    private void updateSponsorUnusedKudos(long sponsorId, int kudosRequest) {
        restTemplate.postForObject(
                "http://SPONSOR/api/v3/sponsors/" + sponsorId,
                kudosRequest,
                Void.class
        );
    }

    private KudosEntity updateSponsorKudosField(ProofEntity proof, TalentEntity follower, long sponsorId,
                                                int kudosRequest, long proofId) {
        if (proof.getKudos().stream()
                .filter(kudos1 -> kudos1.getSponsorId().equals(sponsorId))
                .collect(Collectors.toSet()).isEmpty()) {
            if (kudosRequest < 0) throw new YouCanNotReturnMoreKudosThanGaveException();
            var kudosBuild = KudosEntity.builder()
                    .followerId(follower.getTalentId())
                    .createData(Instant.now())
                    .proof(proof)
                    .sponsorId(sponsorId)
                    .countKudos(kudosRequest)
                    .build();
            kudosRepository.save(kudosBuild);
            return kudosBuild;
        }
        var kudos = kudosRepository.findBySponsorIdAndProof_ProofId(sponsorId, proofId);
        if (kudos.getCountKudos() + kudosRequest < 0) {
            throw new YouCanNotReturnMoreKudosThanGaveException();
        }
        kudos.setCountKudos(kudos.getCountKudos() + kudosRequest);
        kudos.setUpdateData(Instant.now());
        kudosRepository.save(kudos);
        if (kudos.getCountKudos() == 0) {
            kudosRepository.delete(kudos);
        }
        return kudos;
    }
}