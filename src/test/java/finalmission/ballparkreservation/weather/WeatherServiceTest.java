package finalmission.ballparkreservation.weather;

import finalmission.ballparkreservation.external.WeatherClient;
import finalmission.ballparkreservation.external.dto.WeatherResponse;
import finalmission.ballparkreservation.external.dto.WeatherResponse.Body;
import finalmission.ballparkreservation.external.dto.WeatherResponse.Item;
import finalmission.ballparkreservation.external.dto.WeatherResponse.Items;
import finalmission.ballparkreservation.external.dto.WeatherResponse.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    @DisplayName("현재 강수 확률과 기온을 확인할 수 있다")
    void getCurrentWeather() {
        // given
        WeatherResponse weatherResponse = new WeatherResponse(new Response(new Body(new Items(List.of(
                new Item("POP", "20"),
                new Item("TMP", "30")
        )))));

        given(weatherClient.checkWeather())
                .willReturn(weatherResponse);

        // when & then
        assertAll(
                () -> assertThat(weatherService.getCurrentWeather().probabilityOfRain()).isEqualTo(20F),
                () -> assertThat(weatherService.getCurrentWeather().temperature()).isEqualTo(30F)
        );
    }

    @Test
    @DisplayName("날씨 api의 응답 결과 강수 코드를 찾을 수 없는 경우 예외를 발생시킨다")
    void getCurrentWeather_rainCodeError() {
        // given
        WeatherResponse weatherResponse = new WeatherResponse(new Response(new Body(new Items(List.of(
                new Item("TMP", "30")
        )))));

        given(weatherClient.checkWeather())
                .willReturn(weatherResponse);

        // when & then
        assertThatThrownBy(weatherService::getCurrentWeather)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("날씨 api의 응답 결과 기온 코드를 찾을 수 없는 경우 예외를 발생시킨다")
    void getCurrentWeather_temperatureCodeError() {
        // given
        WeatherResponse weatherResponse = new WeatherResponse(new Response(new Body(new Items(List.of(
                new Item("POP", "20")
        )))));

        given(weatherClient.checkWeather())
                .willReturn(weatherResponse);

        // when & then
        assertThatThrownBy(weatherService::getCurrentWeather)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("날씨 api의 응답 결과 강수 확률이 숫자가 아닌 경우 예외를 발생시킨다")
    void getCurrentWeather_rainValueError() {
        // given
        WeatherResponse weatherResponse = new WeatherResponse(new Response(new Body(new Items(List.of(
                new Item("POP", "20퍼센트"),
                new Item("TMP", "30")
        )))));

        given(weatherClient.checkWeather())
                .willReturn(weatherResponse);

        // when & then
        assertThatThrownBy(weatherService::getCurrentWeather)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("날씨 api의 응답 결과 기온이 숫자가 아닌 경우 예외를 발생시킨다")
    void getCurrentWeather_temperatureValueError() {
        // given
        WeatherResponse weatherResponse = new WeatherResponse(new Response(new Body(new Items(List.of(
                new Item("POP", "20"),
                new Item("TMP", "30퍼센트")
        )))));

        given(weatherClient.checkWeather())
                .willReturn(weatherResponse);

        // when & then
        assertThatThrownBy(weatherService::getCurrentWeather)
                .isInstanceOf(IllegalArgumentException.class);
    }
}
