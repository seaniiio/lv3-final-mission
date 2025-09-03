package finalmission.ballparkreservation.external.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HolidayCountResponse {

    private Response response;

    public int getCount() {
        return response.body.totalCount;
    }

    @Getter
    @Setter
    static class Response {

        private Body body;

        @Getter
        @Setter
        static class Body {
            private int totalCount;
        }
    }
}
