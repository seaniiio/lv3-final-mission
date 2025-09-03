package finalmission.ballparkreservation.external;

import finalmission.ballparkreservation.external.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class WeatherClient {

    private final RestClient restClient;

    private static final String BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    private static final String SERVICE_KEY = "SOMj9u0mLcWBW4CqEjVKFOmKCj7eMBHoDVRsRlr0P6kpxQslfjcJ%2FSnS0XDeC1%2FHNy%2BVGxOHCxf7FsAy6FjEnw%3D%3D";
    private static final String NX = "58";
    private static final String NY = "125";

    public WeatherResponse checkWeather() {

        LocalDate date = LocalDate.now();
        if (LocalTime.now().isBefore(LocalTime.of(5, 0))) {
            date = date.minusDays(1);
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 14)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", date.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .queryParam("base_time", "0500")
                .queryParam("nx", NX)
                .queryParam("ny", NY)
                .build(true).toUri();

        ResponseEntity<WeatherResponse> response = restClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(WeatherResponse.class);

        return response.getBody();
    }
}
