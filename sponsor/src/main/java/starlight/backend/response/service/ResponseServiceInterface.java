package starlight.backend.response.service;

import starlight.backend.response.model.response.Response;

public interface ResponseServiceInterface {
    Response creatResponse(long talentId, long vacancyId);

    void deleteResponse(long talentId, long vacancyId);
}
