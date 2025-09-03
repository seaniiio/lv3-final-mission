package finalmission.ballparkreservation.weather;

import finalmission.ballparkreservation.weather.dto.CurrentWeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<CurrentWeatherResponse> getCurrentWeather() {
        CurrentWeatherResponse currentWeather = weatherService.getCurrentWeather();
        return ResponseEntity.ok(currentWeather);
    }
}
