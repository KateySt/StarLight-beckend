package starlight.backend.talent.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import starlight.backend.exception.EmailAlreadyOccupiedException;
import starlight.backend.exception.PageNotFoundException;
import starlight.backend.exception.proof.InvalidStatusException;
import starlight.backend.exception.user.UserNotFoundException;
import starlight.backend.exception.user.talent.TalentNotFoundException;
import starlight.backend.kudos.repository.KudosRepository;
import starlight.backend.proof.ProofRepository;
import starlight.backend.proof.model.entity.ProofEntity;
import starlight.backend.proof.model.enums.Status;
import starlight.backend.talent.MapperTalent;
import starlight.backend.talent.model.entity.PositionEntity;
import starlight.backend.talent.model.entity.TalentEntity;
import starlight.backend.talent.model.request.NewUser;
import starlight.backend.talent.model.request.TalentUpdateRequest;
import starlight.backend.talent.model.response.Talent;
import starlight.backend.talent.model.response.TalentFullInfo;
import starlight.backend.talent.model.response.TalentPagePagination;
import starlight.backend.talent.model.response.TalentPagePaginationWithFilterSkills;
import starlight.backend.talent.repository.PositionRepository;
import starlight.backend.talent.repository.TalentRepository;
import starlight.backend.talent.service.TalentServiceInterface;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
@Slf4j
public class TalentServiceImpl implements TalentServiceInterface {
    private KudosRepository kudosRepository;
    private MapperTalent talentMapper;
    private TalentRepository talentRepository;
    private PositionRepository positionRepository;
    private ProofRepository proofRepository;
    private final String filterParam = "talentId";

    @Override
    public TalentPagePagination talentPagination(int page, int size) {
        var pageRequest = talentRepository.findAll(
                PageRequest.of(page, size, Sort.by(filterParam).descending())
        );
        if (page >= pageRequest.getTotalPages())
            throw new PageNotFoundException(page);
        return talentMapper.toTalentPagePagination(pageRequest);
    }

    @Override
    public TalentFullInfo talentFullInfo(long id) {
        return talentRepository.findById(id)
                .map(talentMapper::toTalentFullInfo)
                .orElseThrow(() -> new TalentNotFoundException(id));
    }


    @Override
    public TalentFullInfo updateTalentProfile(long id, TalentUpdateRequest talentUpdateRequest) {
        return talentRepository.findById(id).map(talent -> {
            talent.setFullName(validationField(
                    talentUpdateRequest.fullName(),
                    talent.getFullName()));
            talent.setBirthday(talentUpdateRequest.avatar() == null ?
                    talent.getBirthday() :
                    talentUpdateRequest.birthday());
            talent.setAvatar(validationField(
                    talentUpdateRequest.avatar(),
                    talent.getAvatar()));
            talent.setEducation(validationField(
                    talentUpdateRequest.education(),
                    talent.getEducation()));
            talent.setExperience(validationField(
                    talentUpdateRequest.experience(),
                    talent.getExperience()));
            talent.setPositions(validationPosition(
                    talent.getPositions(),
                    talentUpdateRequest.positions()));
            talentRepository.save(talent);
            return talentMapper.toTalentFullInfo(talent);
        }).orElseThrow(() -> new TalentNotFoundException(id));
    }

    private String validationField(String newParam, String lastParam) {
        return newParam == null ?
                lastParam :
                newParam;
    }

    private Set<PositionEntity> validationPosition(Set<PositionEntity> talentPositions,
                                                   List<String> positions) {
        if (positions == null) {
            return Collections.emptySet();
        }
        Set<PositionEntity> newPosition = positions.stream()
                .map(position -> {
                    if (position != null && !position.isEmpty()) {
                        PositionEntity pos;
                        if (positionRepository.existsByPositionIgnoreCase(position)) {
                            pos = positionRepository.findByPosition(position);
                        } else {
                            pos = new PositionEntity(position);
                        }
                        return pos;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        newPosition.addAll(talentPositions);
        return !newPosition.isEmpty() ? newPosition : talentPositions;
    }

    @Override
    public void deleteTalentProfile(long talentId) {
        TalentEntity talent = talentRepository.findById(talentId)
                .orElseThrow(() -> new UserNotFoundException(talentId));
        talent.getPositions().clear();
        talent.getTalentSkills().clear();
        if (!talent.getProofs().isEmpty()) {
            for (ProofEntity proof : proofRepository.findByTalent_TalentId(talentId)) {
                proof.setTalent(null);
                proof.getSkills().clear();
                proof.getKudos().clear();
                kudosRepository.deleteAll(proof.getKudos());
                proofRepository.deleteById(proof.getProofId());
            }
        }
        talent.getProofs().clear();
        talentRepository.deleteById(talent.getTalentId());
    }


    @Override
    public TalentPagePaginationWithFilterSkills talentPaginationWithFilter(String filter, int skip, int limit) {
        var talentStream = talentRepository.findAll(
                PageRequest.of(skip, limit, Sort.by(filterParam).descending()));

        if (!filter.isEmpty()) {
            List<TalentEntity> filteredTalents = talentStream.stream()
                    .filter(talent -> talent.getTalentSkills().stream()
                            .anyMatch(skill -> skill.getSkill()
                                    .toLowerCase()
                                    .contains(filter.toLowerCase())
                            )
                    )
                    .collect(Collectors.toList());
            return talentMapper.toTalentListWithPaginationAndFilter(
                    new PageImpl<>(filteredTalents, PageRequest.of(skip, limit), filteredTalents.size())
            );
        }
        return talentMapper.toTalentListWithPaginationAndFilter(talentStream);
    }

    @Override
    public void isStatusCorrect(String status) {
        if (!Arrays.toString(Status.values())
                .matches(".*" + Pattern.quote(status) + ".*")) {
            throw new InvalidStatusException(status);
        }
    }

    @Override
    public Talent saveTalent(NewUser user) {
        if (talentRepository.existsByEmail(user.email())) {
            throw new EmailAlreadyOccupiedException(user.email());
        }
        var talent = talentRepository.save(TalentEntity.builder()
                .email(user.email())
                .password(user.password())
                .fullName(user.fullName())
                .build());
        return talentMapper.toTalent(talent);
    }

    @Override
    public Talent getTalentByEmail(String email) {
        var talent = talentRepository.findByEmail(email);
        return talentMapper.toTalent(talent);
    }

    @Override
    public boolean isTalentExistedById(long talentId) {
        return talentRepository.existsByTalentId(talentId);
    }

    @Override
    public void deletePosition(long talentId, long positionId) {
        if (!positionRepository.existsByPositionIdAndUsers_TalentId(positionId, talentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "don`t found position by talentId and positionId!");
        }
        var position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "don`t found this position"));
        var talent = talentRepository.findById(talentId)
                .orElseThrow(() -> new UserNotFoundException(talentId));
        talent.getPositions().remove(position);
    }
}
