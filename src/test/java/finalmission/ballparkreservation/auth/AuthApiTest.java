package finalmission.ballparkreservation.auth;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthApiTest {

    private final JdbcTemplate jdbcTemplate;
    private final int port;

    public AuthApiTest(
            @LocalServerPort final int port,
            @Autowired final JdbcTemplate jdbcTemplate
    ) {
        this.port = port;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM member");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
    }

    @Nested
    @DisplayName("/auth/signup POST 회원가입 테스트")
    class Signup {

        @DisplayName("회원가입 성공 시 201 코드를 응답한다.")
        @Test
        void signup_success() {
            // when & then
            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", "may@gmail.com", "password", "1234", "name", "메이", "age", 24))
                    .when().post("/auth/signup")
                    .then().statusCode(201);
        }

        @DisplayName("이미 존재하는 이메일로 회원가입 시도 시 400 코드를 응답한다.")
        @Test
        void signup_emailDuplicatedError() {
            // when & then
            RestAssured.given().port(port)
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", "may@gmail.com", "password", "1234", "name", "메이", "age", 24))
                    .when().post("/auth/signup");

            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", "may@gmail.com", "password", "1234", "name", "메이", "age", 24))
                    .when().post("/auth/signup")
                    .then().statusCode(400);
        }
    }

    @Nested
    @DisplayName("/auth/login POST 로그인 테스트")
    class Login {

        @DisplayName("로그인 성공 시 200 코드와 토큰이 담긴 쿠키를 응답한다.")
        @Test
        void login_success() {
            // given
            Map<String, Object> parameters = Map.of("email", "may@gmail.com", "password", "1234", "name", "메이", "age", 24);
            signupForTest(parameters);

            // when & then
            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", "may@gmail.com", "password", "1234"))
                    .when().post("/auth/login")
                    .then().statusCode(200)
                    .header(HttpHeaders.SET_COOKIE.toString(), notNullValue());
        }

        @DisplayName("올바르지 않은 이메일을 입력하면 400 코드를 응답한다.")
        @Test
        void login_emailWrongError() {
            // given
            Map<String, Object> parameters = Map.of("email", "may@gmail.com", "password", "1234", "name", "메이", "age", 24);
            signupForTest(parameters);
            String wrongEmail = "siwon@gmail.com";

            // when & then
            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", wrongEmail, "password", "1234"))
                    .when().post("/auth/login")
                    .then().statusCode(400)
                    .header(HttpHeaders.SET_COOKIE.toString(), nullValue());
        }

        @DisplayName("올바르지 않은 비밀번호를 입력하면 400 코드를 응답한다.")
        @Test
        void login_passwordWrongError() {
            // given
            Map<String, Object> parameters = Map.of("email", "may@gmail.com", "password", "1234", "name", "메이", "age", 24);
            signupForTest(parameters);
            String wrongPassword = "abcd";

            // when & then
            RestAssured.given().port(port).log().all()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", "may@gmail.com", "password", wrongPassword))
                    .when().post("/auth/login")
                    .then().statusCode(400)
                    .header(HttpHeaders.SET_COOKIE.toString(), nullValue());
        }
    }

    private void signupForTest(Map<String, Object> parameter) {
        RestAssured.given().port(port).log().all()
                .contentType(ContentType.JSON)
                .body(parameter)
                .when().post("/auth/signup")
                .then().statusCode(201);
    }
}
