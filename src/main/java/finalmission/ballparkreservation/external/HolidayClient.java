package finalmission.ballparkreservation.external;

import finalmission.ballparkreservation.external.dto.HolidayCountResponse;
import finalmission.ballparkreservation.external.dto.HolidayPluralResponse;
import finalmission.ballparkreservation.external.dto.HolidaySingularResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class HolidayClient {

    private static final String REQUEST_URL = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";

    private static final String KEY = "SOMj9u0mLcWBW4CqEjVKFOmKCj7eMBHoDVRsRlr0P6kpxQslfjcJ%2FSnS0XDeC1%2FHNy%2BVGxOHCxf7FsAy6FjEnw%3D%3D";
    private final RestClient restClient;

    public List<LocalDate> getHolidaysOfYearAndMonth(final LocalDate localDate) {
        int monthValue = localDate.getMonth().getValue();
        String monthVariable = String.valueOf(monthValue);
        if (monthValue < 10) {
            monthVariable = "0" + monthValue;
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(REQUEST_URL)
                .queryParam("serviceKey", KEY)
                .queryParam("solYear", localDate.getYear())
                .queryParam("solMonth", monthVariable)
                .queryParam("_type", "json")
                .build(true).toUri();

        System.out.println("uri = " + uri);

        final ResponseSpec response = restClient.get()
                .uri(uri)
                .retrieve();

        HolidayCountResponse count = response.toEntity(HolidayCountResponse.class).getBody();
        if (count.getCount() > 1) {
            return response.toEntity(HolidayPluralResponse.class).getBody().getHolidays();
        }
        if (count.getCount() == 1) {
            return response.toEntity(HolidaySingularResponse.class).getBody().getHolidays();
        }
        return List.of();
    }
}
