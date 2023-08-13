package starlight.backend.vacancy.service;

import starlight.backend.vacancy.model.request.NewVacancy;
import starlight.backend.vacancy.model.request.VacancyUpdateRequest;
import starlight.backend.vacancy.model.response.Vacancy;
import starlight.backend.vacancy.model.response.VacancyFullInfo;
import starlight.backend.vacancy.model.response.VacancyPagePagination;

public interface VacancyServiceInterface {
    VacancyFullInfo creatVacancy(NewVacancy newVacancy);

    VacancyPagePagination vacancyPagination(int page, int size);

    VacancyFullInfo vacancyFullInfo(long vacancyId);

    VacancyFullInfo updateVacancyProfile(long vacancyId, VacancyUpdateRequest vacancyUpdateRequest);

    void deleteVacancyProfile(long vacancyId);

    VacancyPagePagination vacancyPaginationFromSponsor(int page, int size, long sponsorId);
}
