package finalmission.ballparkreservation.reservation.dto;

import finalmission.ballparkreservation.reservation.Reservation;

public record ReservationCreateResponse(
        int amount
) {

    public static ReservationCreateResponse from(final Reservation savedReservation) {
        return new ReservationCreateResponse(savedReservation.getAmount());
    }
}
