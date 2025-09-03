package finalmission.ballparkreservation.external;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class WeatherClientTest {

    @Autowired
    private WeatherClient weatherClient;

    @Test
    @DisplayName("날씨를 확인하는 api를 호출할 수 있다")
    void checkWeather() {
        // when & then
        assertThat(weatherClient.checkWeather().getResponse().getBody().getItems().getItem().size())
                .isEqualTo(14);
    }
}
