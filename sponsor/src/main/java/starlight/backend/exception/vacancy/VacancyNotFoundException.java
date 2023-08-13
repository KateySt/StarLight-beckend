package starlight.backend.exception.vacancy;

public class VacancyNotFoundException extends RuntimeException {
    public VacancyNotFoundException(long vacancyId) {
        super("vacancy not found by id : " + vacancyId);
    }
}
