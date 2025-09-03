package finalmission.ballparkreservation.weather.dto;

public record CurrentWeatherResponse(
        Float probabilityOfRain,
        Float temperature
) {
}
