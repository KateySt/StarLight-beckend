package starlight.backend.proof.service.impl;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import starlight.backend.exception.PageNotFoundException;
import starlight.backend.exception.proof.ProofNotFoundException;
import starlight.backend.exception.proof.UserCanNotEditProofNotInDraftException;
import starlight.backend.exception.user.UserNotFoundException;
import starlight.backend.exception.user.talent.TalentNotFoundException;
import starlight.backend.kudos.model.entity.KudosEntity;
import starlight.backend.kudos.repository.KudosRepository;
import starlight.backend.proof.ProofMapper;
import starlight.backend.proof.ProofRepository;
import starlight.backend.proof.model.entity.ProofEntity;
import starlight.backend.proof.model.enums.Status;
import starlight.backend.proof.model.request.ProofAddRequest;
import starlight.backend.proof.model.request.ProofAddWithSkillsRequest;
import starlight.backend.proof.model.request.ProofUpdateRequest;
import starlight.backend.proof.model.response.ProofFullInfo;
import starlight.backend.proof.model.response.ProofFullInfoWithSkills;
import starlight.backend.proof.model.response.ProofPagePagination;
import starlight.backend.proof.model.response.ProofPagePaginationWithSkills;
import starlight.backend.proof.service.ProofServiceInterface;
import starlight.backend.skill.service.SkillServiceInterface;
import starlight.backend.talent.repository.TalentRepository;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;

@AllArgsConstructor
@Service
@Transactional
@Slf4j
public class ProofServiceImpl implements ProofServiceInterface {
    private final String DATA_CREATED = "dateCreated";
    private ProofRepository repository;
    private TalentRepository talentRepository;
    private ProofMapper mapper;
    private KudosRepository kudosRepository;
    private SkillServiceInterface skillService;

    @Override
    public ProofPagePagination proofsPagination(int page, int size, boolean sort) {
        var pageRequest = repository.findByStatus(
                Status.PUBLISHED,
                PageRequest.of(page, size, doSort(sort, DATA_CREATED)));
        if (page >= pageRequest.getTotalPages())
            throw new PageNotFoundException(page);
        return mapper.toProofPagePagination(pageRequest);
    }

    @Override
    public ProofPagePaginationWithSkills proofsPaginationWithSkills(int page, int size, boolean sort) {
        var pageRequest = repository.findByStatus(
                Status.PUBLISHED,
                PageRequest.of(page, size, doSort(sort, DATA_CREATED)));
        if (page >= pageRequest.getTotalPages())
            throw new PageNotFoundException(page);
        return mapper.toProofPagePaginationWithSkills(pageRequest);
    }

    @Override
    public ResponseEntity<?> getLocationForAddProofWithSkill(long talentId,
                                                             ProofAddWithSkillsRequest proofAddWithSkillsRequest) {
        long proofId = addProofProfileWithSkill(talentId, proofAddWithSkillsRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{proof-id}")
                .buildAndExpand(proofId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Override
    public long addProofProfileWithSkill(long talentId,
                                         ProofAddWithSkillsRequest proofAddWithSkillsRequest) {
        var talent = talentRepository.findById(talentId)
                .orElseThrow(() -> new ProofNotFoundException(talentId));
        talent.setTalentSkills(skillService.existsSkill(
                talent.getTalentSkills(),
                proofAddWithSkillsRequest.skills()));
        talentRepository.save(talent);

        var proof = repository.save(ProofEntity.builder()
                .title(proofAddWithSkillsRequest.title())
                .description(proofAddWithSkillsRequest.description())
                .link(proofAddWithSkillsRequest.link())
                .status(Status.DRAFT)
                .dateCreated(Instant.now())
                .talent(talentRepository.findById(talentId)
                        .orElseThrow(() -> new TalentNotFoundException(talentId)))
                .skills(proofAddWithSkillsRequest.skills().stream()
                        .map(skill -> skillService.skillValidation(skill))
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList())
                .build());
        return proof.getProofId();
    }

    @Override
    @Transactional(readOnly = true)
    public ProofPagePagination getTalentAllProofsWithKudoses(long talentId,
                                                             int page, int size, boolean sort, String status) {
        Page<ProofEntity> pageRequest = getPaginationForTheTalent(talentId, page,
                size, sort, status);

        return mapper.toProofPagePaginationWithProofFullInfoWithKudoses(pageRequest);
    }

    @Override
    public ProofEntity addProofProfile(long talentId, ProofAddRequest proofAddRequest) {
        return repository.save(ProofEntity.builder()
                .title(proofAddRequest.title())
                .description(proofAddRequest.description())
                .link(proofAddRequest.link())
                .status(Status.DRAFT)
                .dateCreated(Instant.now())
                .talent(talentRepository.findById(talentId)
                        .orElseThrow(() -> new TalentNotFoundException(talentId)))
                .build());
    }

    @Override
    public ResponseEntity<?> getLocation(long talentId,
                                         ProofAddRequest proofAddRequest) {
        long proofId = addProofProfile(talentId, proofAddRequest).getProofId();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{proof-id}")
                .buildAndExpand(proofId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Override
    public ProofFullInfo proofUpdateRequest(long talentId, long id, ProofUpdateRequest proofUpdateRequest) {
        if (!repository.existsByTalent_TalentIdAndProofId(talentId, id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "don`t have that talent");
        }
        var proofEntity = repository.findById(id)
                .orElseThrow(() -> new ProofNotFoundException(id));
        var talent = talentRepository.findById(talentId)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (!proofEntity.getStatus().equals(Status.DRAFT)
                && proofUpdateRequest.status().equals(Status.DRAFT)) {
            throw new UserCanNotEditProofNotInDraftException();
        }
        if (proofEntity.getStatus().equals(Status.DRAFT)) {
            return changeStatusFromDraft(proofUpdateRequest, proofEntity);
        }
        if (proofUpdateRequest.status().equals(Status.HIDDEN)
                || proofUpdateRequest.status().equals(Status.PUBLISHED)) {
            proofEntity.setStatus(proofUpdateRequest.status());
        }
        proofEntity.setSkills(skillService.existsSkill(
                proofEntity.getSkills(),
                proofUpdateRequest.skills()));
        talent.setTalentSkills(skillService.existsSkill(
                proofEntity.getSkills(),
                proofUpdateRequest.skills()));
        talentRepository.save(talent);
        proofEntity.setDateLastUpdated(Instant.now());
        repository.save(proofEntity);
        return mapper.toProofFullInfo(proofEntity);
    }

    private ProofFullInfo changeStatusFromDraft(ProofUpdateRequest proofUpdateRequest, ProofEntity proofEntity) {
        proofEntity.setTitle(validationField(
                proofUpdateRequest.title(),
                proofEntity.getTitle()));
        proofEntity.setDescription(validationField(
                proofUpdateRequest.description(),
                proofEntity.getDescription()));
        proofEntity.setLink(validationField(
                proofUpdateRequest.link(),
                proofEntity.getLink()));
        proofEntity.setStatus(proofUpdateRequest.status());
        proofEntity.setDateLastUpdated(Instant.now());
        repository.save(proofEntity);
        return mapper.toProofFullInfo(proofEntity);
    }

    private String validationField(String newParam, String lastParam) {
        return newParam == null ?
                lastParam :
                newParam;
    }

    @Override
    public void deleteProof(long talentId, long proofId) {
        ProofEntity proof = repository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));
        proof.setTalent(null);
        for (KudosEntity kudos : kudosRepository.findByProof_ProofId(proofId)) {
            kudos.setProof(null);
            kudosRepository.deleteById(kudos.getKudosId());
        }
        proof.getKudos().clear();
        repository.deleteById(proofId);
    }

    @Override
    public ProofPagePagination getTalentAllProofs(long talentId,
                                                  int page, int size, boolean sort, String status) {
        var pageRequest = getPaginationForTheTalent(talentId, page,
                size, sort, status);
        return mapper.toProofPagePagination(pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ProofPagePaginationWithSkills getTalentAllProofsWithSkills(long talentId,
                                                                      int page, int size, boolean sort, String status) {
        var pageRequest = getPaginationForTheTalent(talentId, page,
                size, sort, status);
        return mapper.toProofPagePaginationWithSkills(pageRequest);
    }

    private Page<ProofEntity> getPaginationForTheTalent(long talentId, int page, int size,
                                                        boolean sort, String status) {
        return (status.equals(Status.ALL.getStatus())) ?
                repository.findByTalent_TalentId(talentId,
                        PageRequest.of(page, size, doSort(sort, DATA_CREATED)))
                :
                repository.findByTalent_TalentIdAndStatus(talentId, Status.valueOf(status),
                        PageRequest.of(page, size, doSort(sort, DATA_CREATED)));
    }

    @Override
    public ProofFullInfo getProofFullInfo(long proofId) {
        if (!repository.existsById(proofId)) {
            throw new ProofNotFoundException(proofId);
        }
        ProofEntity proof = repository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));
        return mapper.toProofFullInfo(proof);
    }

    @Override
    public ProofFullInfoWithSkills getProofFullInfoWithSkills(long proofId) {
        if (!repository.existsById(proofId)) {
            throw new ProofNotFoundException(proofId);
        }
        ProofEntity proof = repository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));
        return mapper.toProofFullInfoWithSkills(proof);
    }

    public Sort doSort(boolean sort, String sortParam) {
        Sort dateSort;
        if (sort) {
            dateSort = Sort.by(sortParam).descending();
        } else {
            dateSort = Sort.by(sortParam);
        }
        return dateSort;
    }
}

