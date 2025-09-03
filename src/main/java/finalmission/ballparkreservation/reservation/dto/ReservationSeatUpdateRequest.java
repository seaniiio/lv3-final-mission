package finalmission.ballparkreservation.reservation.dto;

import jakarta.validation.constraints.NotNull;

public record ReservationSeatUpdateRequest(
        @NotNull Long id,
        @NotNull Integer seatNumber
) {
}
