package academy.tochkavhoda.competition.api;

import academy.tochkavhoda.competition.api.dto.requests.*;
import academy.tochkavhoda.competition.api.dto.responses.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestRegistrationAndLogin extends TestBase {

    @Test
    public void testRegisterParticipant() {
        ServerResponse response = registerParticipant("participant1");
        assertEquals(200, response.getResponseCode());
        assertEquals("{}", response.getResponseData());
    }

    @Test
    public void testRegisterExpert() {
        assertEquals(200, registerExpert("expert001", "математика", "информатика").getResponseCode());
    }

    @Test
    public void testDuplicateLoginForParticipants() {
        registerParticipant("participant1");
        assertError(registerParticipant("participant1"), "уже используется");
    }

    @Test
    public void testLoginSharedBetweenParticipantAndExpert() {
        registerParticipant("someone01");
        assertError(registerExpert("someone01", "математика"), "уже используется");
    }

    @Test
    public void testInvalidParticipantData() {
        String[] bad = {
                gson.toJson(new RegisterParticipantRequest("", "Иванов", "Фирма", "login123", PASSWORD)),
                gson.toJson(new RegisterParticipantRequest("Иван", null, "Фирма", "login123", PASSWORD)),
                gson.toJson(new RegisterParticipantRequest("Иван", "Иванов", "  ", "login123", PASSWORD))
        };
        for (String json : bad) {
            assertError(server.registerParticipant(json), "не заполнено");
        }
        assertError(server.registerParticipant(gson.toJson(
                new RegisterParticipantRequest("Иван", "Иванов", "Фирма", "abc", PASSWORD))), "Логин");
        assertError(server.registerParticipant(gson.toJson(
                new RegisterParticipantRequest("Иван", "Иванов", "Фирма", "login 123", PASSWORD))), "Логин");
        assertError(server.registerParticipant(gson.toJson(
                new RegisterParticipantRequest("Иван", "Иванов", "Фирма", "login123", "short1"))), "Пароль");
        assertError(server.registerParticipant(gson.toJson(
                new RegisterParticipantRequest("Иван", "Иванов", "Фирма", "login123", "onlyletters"))), "Пароль");
    }

    @Test
    public void testInvalidExpertDirections() {
        assertError(registerExpert("expert001"), "направлений");
        assertError(registerExpert("expert001", "математика", " "), "направлений");
    }

    @Test
    public void testInvalidJson() {
        assertError(server.registerParticipant("это не json"), "формат");
        assertError(server.registerParticipant(""), "формат");
        assertError(server.registerExpert(null), "формат");
        assertError(server.login("{{"), "формат");
    }

    @Test
    public void testLogin() {
        registerParticipant("participant1");
        ServerResponse response = server.login(gson.toJson(new LoginRequest("participant1", PASSWORD)));
        assertEquals(200, response.getResponseCode());
        assertNotNull(gson.fromJson(response.getResponseData(), LoginResponse.class).getToken());
    }

    @Test
    public void testLoginWrongCredentials() {
        registerParticipant("participant1");
        assertError(server.login(gson.toJson(new LoginRequest("participant1", "wrongpass1"))), "Неверный");
        assertError(server.login(gson.toJson(new LoginRequest("unknown123", PASSWORD))), "Неверный");
        assertError(server.login(gson.toJson(new LoginRequest("", PASSWORD))), "не заполнено");
    }

    @Test
    public void testLogoutInvalidatesToken() {
        String token = participantToken("participant1");
        assertEquals(200, server.logout(token).getResponseCode());
        assertError(server.getMyApplications(token), "токен");
        assertError(server.logout(token), "токен");
    }

    @Test
    public void testRelogin() {
        String first = participantToken("participant1");
        server.logout(first);
        String second = login("participant1");
        assertNotEquals(first, second);
        assertEquals(200, server.getMyApplications(second).getResponseCode());
    }

    @Test
    public void testLoginWithoutLogoutInvalidatesOldToken() {
        String first = participantToken("participant1");
        String second = login("participant1");
        assertNotEquals(first, second);
        assertError(server.getMyApplications(first), "токен");
        assertEquals(200, server.getMyApplications(second).getResponseCode());
    }

    @Test
    public void testFakeToken() {
        assertError(server.getMyApplications("fake-token"), "токен");
        assertError(server.getMyApplications(null), "токен");
    }

    @Test
    public void testLeaveRemovesUser() {
        String token = participantToken("participant1");
        addApplication(token, "Проект", 100, "математика");
        assertEquals(200, server.leave(token).getResponseCode());
        assertError(server.getMyApplications(token), "токен");
        assertError(server.login(gson.toJson(new LoginRequest("participant1", PASSWORD))), "Неверный");
        // логин освободился, можно зарегистрироваться снова - заявок у нового участника нет
        String again = participantToken("participant1");
        assertEquals(0, applications(server.getMyApplications(again)).size());
    }
}
