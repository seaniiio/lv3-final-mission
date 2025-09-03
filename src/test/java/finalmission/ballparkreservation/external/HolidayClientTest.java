package finalmission.ballparkreservation.external;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class HolidayClientTest {

    @Autowired
    private HolidayClient holidayClient;

    @Test
    @DisplayName("2025년 5월에 대한 공휴일 목록을 확인할 수 있다 - 2025/05/05, 2025/05/06")
    void getHolidaysOfYearAndMonth_pluralHolidays() {
        // given
        final LocalDate date = LocalDate.of(2025, 5, 1);

        // when & then
        Assertions.assertThat(holidayClient.getHolidaysOfYearAndMonth(date))
                .containsExactly(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 6));
    }

    @Test
    @DisplayName("2025년 8월에 대한 공휴일 목록을 확인할 수 있다 - 2025/08/15")
    void getHolidaysOfYearAndMonth_singularHoliday() {
        // given
        final LocalDate date = LocalDate.of(2025, 8, 1);

        // when & then
        Assertions.assertThat(holidayClient.getHolidaysOfYearAndMonth(date))
                .containsExactly(LocalDate.of(2025, 8, 15));
    }

    @Test
    @DisplayName("2025년 7월에 대한 공휴일 목록을 확인할 수 있다 - 없음")
    void getHolidaysOfYearAndMonth_holidayNotExists() {
        // given
        final LocalDate date = LocalDate.of(2025, 7, 1);

        // when & then
        Assertions.assertThat(holidayClient.getHolidaysOfYearAndMonth(date))
                .isEmpty();
    }
}
