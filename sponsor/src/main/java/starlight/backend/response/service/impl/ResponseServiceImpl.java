package starlight.backend.response.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import starlight.backend.exception.user.UserNotFoundException;
import starlight.backend.exception.vacancy.VacancyNotFoundException;
import starlight.backend.response.ResponseMapper;
import starlight.backend.response.model.entity.ResponseEntity;
import starlight.backend.response.model.response.Response;
import starlight.backend.response.repository.ResponseRepository;
import starlight.backend.response.repository.TalentRepository;
import starlight.backend.response.service.ResponseServiceInterface;
import starlight.backend.vacancy.VacancyRepository;

import java.time.Instant;

@Service
@AllArgsConstructor
public class ResponseServiceImpl implements ResponseServiceInterface {
    private ResponseRepository responseRepository;
    private VacancyRepository vacancyRepository;
    private TalentRepository talentRepository;
    private ResponseMapper responseMapper;

    @Override
    public Response creatResponse(long talentId, long vacancyId) {
        var vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new VacancyNotFoundException(vacancyId));
        var talent = talentRepository.findById(talentId)
                .orElseThrow(() -> new UserNotFoundException(talentId));
        var response = responseRepository.save(ResponseEntity.builder()
                .vacancy(vacancy)
                .talent(talent)
                .responseDateCreate(Instant.now())
                .build());
        return responseMapper.toResponse(response);
    }

    @Override
    public void deleteResponse(long talentId, long vacancyId) {
        if (!responseRepository.existsByTalent_TalentIdAndVacancy_VacancyId(talentId, vacancyId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "don`t found response by talentId and vacancyId!");
        }
        var response = responseRepository.findByTalent_TalentIdAndVacancy_VacancyId(talentId, vacancyId);
        response.setTalent(null);
        response.setVacancy(null);
        responseRepository.delete(response);
    }
}
