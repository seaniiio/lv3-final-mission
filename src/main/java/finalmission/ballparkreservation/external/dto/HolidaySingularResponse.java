package finalmission.ballparkreservation.external.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class HolidaySingularResponse {

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

            Item item;
        }

        @Getter
        @Setter
        static class Item {

            String locdate;
        }
    }

    public List<LocalDate> getHolidays() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return List.of(LocalDate.parse(response.body.items.item.locdate, formatter));
    }
}
