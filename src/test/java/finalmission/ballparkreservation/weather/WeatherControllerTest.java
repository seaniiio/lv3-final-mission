package finalmission.ballparkreservation.weather;

import finalmission.ballparkreservation.auth.JwtProvider;
import finalmission.ballparkreservation.weather.dto.CurrentWeatherResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("현재 날씨를 확인할 수 있다")
    void getWeather() throws Exception {
        // given
        CurrentWeatherResponse response = new CurrentWeatherResponse(20F, 30F);
        given(weatherService.getCurrentWeather())
                .willReturn(response);

        // when & then
        MvcResult mvcResult = mockMvc.perform(get("/weather"))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo("{\"probabilityOfRain\":20.0,\"temperature\":30.0}");
    }
}
