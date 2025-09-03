package finalmission.ballparkreservation.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationCreateRequest(
        @NotBlank String seatRank,
        int seatNumber,
        @NotNull LocalDate date
) {
}
