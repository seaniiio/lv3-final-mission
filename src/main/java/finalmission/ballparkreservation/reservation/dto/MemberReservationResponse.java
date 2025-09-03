package finalmission.ballparkreservation.reservation.dto;

import finalmission.ballparkreservation.reservation.Reservation;
import finalmission.ballparkreservation.schedule.dto.ScheduleResponse;

public record MemberReservationResponse(
        ScheduleResponse schedule,
        int amount
) {

    public static MemberReservationResponse from(final Reservation reservation) {
        return new MemberReservationResponse(ScheduleResponse.from(reservation.getSchedule()), reservation.getAmount());
    }
}
