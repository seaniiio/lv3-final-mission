package finalmission.ballparkreservation.weather;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherApiTest {

    private final int port;

    public WeatherApiTest(
            @LocalServerPort final int port
    ) {
        this.port = port;
    }

    @DisplayName("/weather GET 현재 날씨 확인 테스트")
    @Test
    void getCurrentWeather() {
        // when & then
        RestAssured.given().port(port).log().all()
                .contentType(ContentType.JSON)
                .when().get("/weather")
                .then().statusCode(200);
    }
}
