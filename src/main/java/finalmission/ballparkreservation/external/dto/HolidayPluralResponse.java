package finalmission.ballparkreservation.external.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class HolidayPluralResponse {

    private Response response;

    @Getter
    @Setter
    static class Response {
        Body body;

        @Getter
        @Setter
        static class Body {

            Items items;
        }

        @Getter
        @Setter
        static class Items {

            List<Item> item;
        }

        @Getter
        @Setter
        static class Item {

            String locdate;
        }
    }

    public List<LocalDate> getHolidays() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return this.response.getBody().getItems().getItem().stream()
                .map(info -> LocalDate.parse(info.getLocdate(), formatter))
                .distinct()
                .toList();
    }
}
