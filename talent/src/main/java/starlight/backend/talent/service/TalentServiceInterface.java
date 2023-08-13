package starlight.backend.talent.service;

import starlight.backend.talent.model.request.NewUser;
import starlight.backend.talent.model.request.TalentUpdateRequest;
import starlight.backend.talent.model.response.Talent;
import starlight.backend.talent.model.response.TalentFullInfo;
import starlight.backend.talent.model.response.TalentPagePagination;
import starlight.backend.talent.model.response.TalentPagePaginationWithFilterSkills;

public interface TalentServiceInterface {
    TalentPagePagination talentPagination(int page, int size);

    TalentFullInfo talentFullInfo(long id);

    TalentFullInfo updateTalentProfile(long id, TalentUpdateRequest talentUpdateRequest);

    void deleteTalentProfile(long talentId);

    TalentPagePaginationWithFilterSkills talentPaginationWithFilter(String filter, int skip, int limit);

    void isStatusCorrect(String status);

    Talent saveTalent(NewUser user);

    Talent getTalentByEmail(String email);

    boolean isTalentExistedById(long talentId);

    void deletePosition(long talentId, long positionId);
}