package finalmission.ballparkreservation.weather;

import finalmission.ballparkreservation.external.WeatherClient;
import finalmission.ballparkreservation.external.dto.WeatherResponse;
import finalmission.ballparkreservation.weather.dto.CurrentWeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {

    @Autowired
    private final WeatherClient weatherClient;

    public CurrentWeatherResponse getCurrentWeather() {
        WeatherResponse weatherResponse = weatherClient.checkWeather();

        final Float probabilityOfRain = getWeatherFactor(weatherResponse, "POP");
        final Float temperature = getWeatherFactor(weatherResponse, "TMP");

        return new CurrentWeatherResponse(probabilityOfRain, temperature);
    }

    private Float getWeatherFactor(final WeatherResponse weatherResponse, final String weatherCode) {
        WeatherResponse.Item rain = weatherResponse.getResponse().getBody().getItems().getItem().stream()
                .filter(item -> item.getCategory().equals(weatherCode))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        try {
            return Float.valueOf(rain.getFcstValue());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
    }
}
