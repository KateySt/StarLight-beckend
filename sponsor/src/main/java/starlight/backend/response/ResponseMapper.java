package starlight.backend.response;

import org.mapstruct.Mapper;
import starlight.backend.response.model.entity.ResponseEntity;
import starlight.backend.response.model.response.Response;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface ResponseMapper {
    default Response toResponse(ResponseEntity response) {
        return Response.builder()
                .responseDateCreate(response.getResponseDateCreate())
                .responseId(response.getResponseId())
                .talent(response.getTalent())
                .vacancy(response.getVacancy())
                .build();
    }
}
