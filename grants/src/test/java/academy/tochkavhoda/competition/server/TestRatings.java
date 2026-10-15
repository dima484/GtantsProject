package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.response.ApplicationDtoResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestRatings extends TestBase {

    @Test
    public void testRateAndChangeRating() {
        String p = participantToken("participant1");
        int id = addApplication(p, "Проект", 100, "математика");
        String expert = expertToken("expert001", "математика");

        rateOk(expert, id, 3);
        List<ApplicationDtoResponse> rated = applications(server.getRatedApplications(expert));
        assertEquals(1, rated.size());
        assertEquals(Integer.valueOf(3), rated.get(0).getRating());

        rateOk(expert, id, 5);
        rated = applications(server.getRatedApplications(expert));
        assertEquals(1, rated.size());
        assertEquals(Integer.valueOf(5), rated.get(0).getRating());
    }

    @Test
    public void testRatingVisibleInApplicationList() {
        String p = participantToken("participant1");
        int rated = addApplication(p, "Оцененная", 100, "математика");
        addApplication(p, "Новая", 100, "математика");
        String expert = expertToken("expert001", "математика");
        rateOk(expert, rated, 4);

        List<ApplicationDtoResponse> list = applications(server.getApplications(expert, null));
        assertEquals(Integer.valueOf(4), list.get(0).getRating());
        assertNull(list.get(1).getRating());
    }

    @Test
    public void testInvalidRating() {
        String p = participantToken("participant1");
        int id = addApplication(p, "Проект", 100, "математика");
        String expert = expertToken("expert001", "математика");
        assertError(rate(expert, id, 0), "от 1 до 5");
        assertError(rate(expert, id, 6), "от 1 до 5");
        assertError(rate(expert, id, -1), "от 1 до 5");
        assertEquals(0, applications(server.getRatedApplications(expert)).size());
    }

    @Test
    public void testRateUnknownApplication() {
        String expert = expertToken("expert001", "математика");
        assertError(rate(expert, 12345, 5), "не найдена");
    }

    @Test
    public void testExpertCannotRateOutsideOwnDirections() {
        String p = participantToken("participant1");
        int id = addApplication(p, "Биология", 100, "биология");
        String expert = expertToken("expert001", "математика");
        assertError(rate(expert, id, 5), "не входит");
    }

    @Test
    public void testRemoveRating() {
        String p = participantToken("participant1");
        int id = addApplication(p, "Проект", 100, "математика");
        String expert = expertToken("expert001", "математика");
        rateOk(expert, id, 4);

        String json = gson.toJson(new ApplicationIdDtoRequest(id));
        assertEquals(200, server.removeRating(expert, json).getResponseCode());
        assertEquals(0, applications(server.getRatedApplications(expert)).size());
        assertError(server.removeRating(expert, json), "не найдена");
    }

    @Test
    public void testCancelApplicationRemovesItsRatings() {
        String p = participantToken("participant1");
        int id = addApplication(p, "Проект", 100, "математика");
        String expert = expertToken("expert001", "математика");
        rateOk(expert, id, 4);
        server.cancelApplication(p, gson.toJson(new ApplicationIdDtoRequest(id)));
        assertEquals(0, applications(server.getRatedApplications(expert)).size());
    }

    @Test
    public void testRatingsOfDifferentExpertsAreIndependent() {
        String p = participantToken("participant1");
        int id = addApplication(p, "Проект", 100, "математика");
        String e1 = expertToken("expert001", "математика");
        String e2 = expertToken("expert002", "математика");
        rateOk(e1, id, 2);
        rateOk(e2, id, 5);
        assertEquals(Integer.valueOf(2), applications(server.getRatedApplications(e1)).get(0).getRating());
        assertEquals(Integer.valueOf(5), applications(server.getRatedApplications(e2)).get(0).getRating());
    }

    @Test
    public void testExpertLeavesRatingsDisappear() {
        String p = participantToken("participant1");
        int id = addApplication(p, "Проект", 100, "математика");
        String leaving = expertToken("expert001", "математика");
        String staying = expertToken("expert002", "математика");
        rateOk(leaving, id, 1);
        rateOk(staying, id, 5);
        assertEquals(200, server.leave(leaving).getResponseCode());

        // средняя оценка должна быть 5.0, а не 3.0
        assertEquals(5.0, results(finish(1000, 1)).getGrants().get(0).getAverageRating(), 0.0001);
    }
}
