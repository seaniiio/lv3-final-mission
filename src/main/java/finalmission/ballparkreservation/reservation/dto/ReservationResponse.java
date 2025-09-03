package finalmission.ballparkreservation.reservation.dto;

import finalmission.ballparkreservation.reservation.Reservation;
import finalmission.ballparkreservation.schedule.dto.ScheduleResponse;

public record ReservationResponse(
        ScheduleResponse schedule,
        String memberName
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(ScheduleResponse.from(reservation.getSchedule()), reservation.getMember().getName());
    }
}
