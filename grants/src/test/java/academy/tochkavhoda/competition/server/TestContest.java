package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.response.ContestResultDtoResponse;
import academy.tochkavhoda.competition.dto.response.GrantDtoResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestContest extends TestBase {

    private String participant;
    private String expert1;
    private String expert2;

    private void prepare() {
        participant = participantToken("participant1");
        expert1 = expertToken("expert001", "математика");
        expert2 = expertToken("expert002", "математика");
    }

    private int app(String title, long amount, int rating1, int rating2) {
        int id = addApplication(participant, title, amount, "математика");
        rateOk(expert1, id, rating1);
        rateOk(expert2, id, rating2);
        return id;
    }

    @Test
    public void testSelectionAlgorithm() {
        prepare();
        int a = app("A", 60, 5, 5);    // 5.0
        int b = app("B", 50, 5, 4);    // 4.5 - не хватит денег (остаток 40)
        int c = app("C", 30, 4, 4);    // 4.0
        int e = app("E", 20, 3, 3);    // 3.0 - не хватит денег (остаток 10)
        app("D", 10, 2, 2);            // 2.0 - не выше порога, дальше не распределяем

        ContestResultDtoResponse result = results(finish(100, 2));
        List<GrantDtoResponse> grants = result.getGrants();
        assertEquals(2, grants.size());
        assertEquals(a, grants.get(0).getApplicationId());
        assertEquals(c, grants.get(1).getApplicationId());
        assertEquals(5.0, grants.get(0).getAverageRating(), 0.0001);
        assertEquals(10, result.getRemainingFund());
        assertNotEquals(b, grants.get(1).getApplicationId());
        assertNotEquals(e, grants.get(1).getApplicationId());
    }

    @Test
    public void testSameRatingCheaperFirst() {
        prepare();
        app("Дорогая", 50, 4, 4);
        int cheap = app("Дешевая", 30, 4, 4);

        // если бы дорогая шла первой, она забрала бы 50 из 60, и дешевая (30) уже не поместилась бы
        ContestResultDtoResponse result = results(finish(60, 1));
        assertEquals(1, result.getGrants().size());
        assertEquals(cheap, result.getGrants().get(0).getApplicationId());
        assertEquals(30, result.getRemainingFund());
    }

    @Test
    public void testThresholdIsExclusive() {
        prepare();
        app("A", 10, 3, 3);
        assertEquals(0, results(finish(100, 3)).getGrants().size());
    }

    @Test
    public void testThresholdAllowsHigherRatings() {
        prepare();
        app("A", 10, 3, 4); // 3.5 > 3
        ContestResultDtoResponse result = results(finish(100, 3));
        assertEquals(1, result.getGrants().size());
        assertEquals(90, result.getRemainingFund());
    }

    @Test
    public void testApplicationsWithoutRatingsAreIgnored() {
        prepare();
        addApplication(participant, "Без оценок", 10, "математика");
        ContestResultDtoResponse result = results(finish(100, 1));
        assertEquals(0, result.getGrants().size());
        assertEquals(100, result.getRemainingFund());
    }

    @Test
    public void testAverageUsesOnlyExistingRatings() {
        prepare();
        int id = addApplication(participant, "A", 10, "математика");
        rateOk(expert1, id, 5);
        rateOk(expert2, id, 1);
        server.removeRating(expert2, gson.toJson(new ApplicationIdDtoRequest(id)));
        assertEquals(5.0, results(finish(100, 1)).getGrants().get(0).getAverageRating(), 0.0001);
    }

    @Test
    public void testCancelledApplicationDoesNotParticipate() {
        prepare();
        int id = app("A", 10, 5, 5);
        server.cancelApplication(participant, gson.toJson(new ApplicationIdDtoRequest(id)));
        assertEquals(0, results(finish(100, 1)).getGrants().size());
    }

    @Test
    public void testEverythingIsBlockedAfterFinish() {
        prepare();
        int id = app("A", 10, 5, 5);
        assertEquals(200, finish(100, 1).getResponseCode());

        assertError(addApplicationResponse(participant, "Новая", 10, "математика"), "завершена");
        assertError(server.cancelApplication(participant, gson.toJson(new ApplicationIdDtoRequest(id))), "завершена");
        assertError(rate(expert1, id, 1), "завершена");
        assertError(server.removeRating(expert1, gson.toJson(new ApplicationIdDtoRequest(id))), "завершена");
    }

    @Test
    public void testFinishTwice() {
        prepare();
        assertEquals(200, finish(100, 1).getResponseCode());
        assertError(finish(100, 1), "уже подведены");
    }

    @Test
    public void testInvalidFinishParameters() {
        prepare();
        assertError(finish(0, 1), "Фонд");
        assertError(finish(100, -1), "Фонд");
        assertError(finish(100, 5.5), "Фонд");
        assertError(server.finishContest("не json"), "формат");
        assertEquals(200, finish(100, 1).getResponseCode());
    }

    @Test
    public void testGetResults() {
        prepare();
        app("A", 10, 5, 5);
        assertError(server.getResults(participant), "еще не подведены");
        finish(100, 1);
        ContestResultDtoResponse result = results(server.getResults(participant));
        assertEquals(1, result.getGrants().size());
        assertEquals("Фирма participant1", result.getGrants().get(0).getFirmName());
        assertEquals(90, result.getRemainingFund());
        assertError(server.getResults("fake"), "токен");
    }
}
