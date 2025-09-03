package finalmission.ballparkreservation.external.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class WeatherResponse {
    private Response response;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response {
        private Body body;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Body {
        private Items items;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Items {
        private List<Item> item;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Item {
        private String category;
        private String fcstValue;
    }
}
