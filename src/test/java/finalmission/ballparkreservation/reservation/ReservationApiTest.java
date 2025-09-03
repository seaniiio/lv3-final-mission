package finalmission.ballparkreservation.reservation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationApiTest {

    private final JdbcTemplate jdbcTemplate;
    private final int port;

    public ReservationApiTest(
            @LocalServerPort final int port,
            @Autowired final JdbcTemplate jdbcTemplate
    ) {
        this.port = port;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM reservation");
        jdbcTemplate.update("DELETE FROM schedule");
        jdbcTemplate.update("DELETE FROM member");

        jdbcTemplate.update("ALTER TABLE reservation ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE schedule ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.update("INSERT INTO schedule(number, rank, date) VALUES (1, 'TABLE', '2025-05-05')");
        jdbcTemplate.update("INSERT INTO schedule(number, rank, date) VALUES (2, 'TABLE', '2025-05-05')");
        jdbcTemplate.update("INSERT INTO member(email, password, name, age) VALUES ('may@gmail.com', '1234', '메이', 13)");
        jdbcTemplate.update("INSERT INTO member(email, password, name, age) VALUES ('siwon@gmail.com', '1234', '시원', 13)");
    }

    @Nested
    @DisplayName("/reservations POST 예약 생성 테스트")
    class create {

        @DisplayName("예약 생성 성공 시 200 코드를 응답한다.")
        @Test
        void create_success() {
            Cookie tokenCookie = loginForTest();

            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .cookie(tokenCookie)
                    .body(Map.of("seatRank", "TABLE", "seatNumber", 1, "date", LocalDate.of(2025, 5, 5)))
                    .when().post("/reservations")
                    .then().statusCode(201);
        }

        @DisplayName("로그인 정보가 존재하지 않는 경우 401 코드를 응답한다.")
        @Test
        void create_unauthenticatedError() {
            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .body(Map.of("seatRank", "TABLE", "seatNumber", 1, "date", LocalDate.of(2025, 5, 5)))
                    .when().post("/reservations")
                    .then().statusCode(401);
        }
    }

    @Nested
    @DisplayName("/reservations GET 모든 예약 조회 테스트")
    class getAll {

        @DisplayName("예약 조회 성공 시 200 코드를 응답한다.")
        @Test
        void getAll_success() {
            Cookie cookie = loginForTest();
            createReservationForTest(cookie);

            RestAssured.given().port(port).log().all()
                    .cookie(cookie)
                    .when().get("/reservations")
                    .then().statusCode(200)
                    .body("size()", is(1));
        }

        @DisplayName("로그인 정보가 존재하지 않는 경우 401 코드를 응답한다.")
        @Test
        void getAll_unauthenticatedError() {
            RestAssured.given().port(port).log().all()
                    .when().get("/reservations")
                    .then().statusCode(401);
        }
    }

    @Nested
    @DisplayName("/reservations/my GET 회원별 예약 조회 테스트")
    class getAllByMember {

        @DisplayName("회원별 예약 조회 성공 시 200 코드를 응답한다.")
        @Test
        void getAllByMember_success() {
            Cookie cookie = loginForTest();
            createReservationForTest(cookie);

            RestAssured.given().port(port).log().all()
                    .cookie(cookie)
                    .when().get("/reservations/my")
                    .then().statusCode(200)
                    .body("size()", is(1));
        }

        @DisplayName("로그인 정보가 존재하지 않는 경우 401 코드를 응답한다.")
        @Test
        void getAllByMember_unauthenticatedError() {
            RestAssured.given().port(port).log().all()
                    .when().get("/reservations/my")
                    .then().statusCode(401);
        }
    }

    @Nested
    @DisplayName("/reservations/seat PATCH 예약 좌석 변경 테스트")
    class update {

        @DisplayName("예약 수정 성공 시 200 코드를 응답한다.")
        @Test
        void update_success() {
            Cookie cookie = loginForTest();
            createReservationForTest(cookie);

            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .cookie(cookie)
                    .body(Map.of("id", 1L, "seatNumber", 2))
                    .when().patch("/reservations/seat")
                    .then().statusCode(200);
        }

        @DisplayName("로그인 정보가 존재하지 않는 경우 401 코드를 응답한다.")
        @Test
        void update_unauthenticatedError() {
            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .body(Map.of("id", 1L, "seatNumber", 2))
                    .when().patch("/reservations/seat")
                    .then().statusCode(401);
        }

        @DisplayName("예약 수정 권한이 올바르지 않은 경우 403 코드를 응답한다.")
        @Test
        void update_unauthorizedError() {
            Cookie cookie = loginForTest();
            Cookie anotherMemberCookie = loginAnotherMemberForTest();
            createReservationForTest(cookie);

            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .cookie(anotherMemberCookie)
                    .body(Map.of("id", 1L, "seatNumber", 2))
                    .when().patch("/reservations/seat")
                    .then().statusCode(403);
        }
    }

    @Nested
    @DisplayName("/reservations DELETE 예약 삭제 테스트")
    class delete {

        @DisplayName("예약 삭제 성공 시 200 코드를 응답한다.")
        @Test
        void delete_success() {
            Cookie cookie = loginForTest();
            createReservationForTest(cookie);

            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .cookie(cookie)
                    .when().delete("/reservations/1")
                    .then().statusCode(200);
        }

        @DisplayName("로그인 정보가 존재하지 않는 경우 401 코드를 응답한다.")
        @Test
        void delete_unauthenticatedError() {
            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .when().delete("/reservations/1")
                    .then().statusCode(401);
        }

        @DisplayName("예약 삭제 권한이 올바르지 않은 경우 403 코드를 응답한다.")
        @Test
        void delete_unauthorizedError() {
            Cookie cookie = loginForTest();
            Cookie anotherMemberCookie = loginAnotherMemberForTest();
            createReservationForTest(cookie);

            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .cookie(anotherMemberCookie)
                    .when().delete("/reservations/1")
                    .then().statusCode(403);
        }
    }

    private Cookie loginForTest() {
        return RestAssured.given().port(port).log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "may@gmail.com", "password", "1234"))
                .when().post("/auth/login")
                .then().extract().detailedCookie("token");
    }

    private Cookie loginAnotherMemberForTest() {
        return RestAssured.given().port(port).log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "siwon@gmail.com", "password", "1234"))
                .when().post("/auth/login")
                .then().extract().detailedCookie("token");
    }

    private void createReservationForTest(Cookie cookie) {
        RestAssured.given().port(port).log().all()
                .contentType(ContentType.JSON)
                .cookie(cookie)
                .body(Map.of("seatRank", "TABLE", "seatNumber", 1, "date", LocalDate.of(2025, 5, 5)))
                .when().post("/reservations");
    }
}
