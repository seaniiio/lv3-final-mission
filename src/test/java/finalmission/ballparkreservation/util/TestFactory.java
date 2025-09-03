package finalmission.ballparkreservation.util;

import finalmission.ballparkreservation.member.Member;
import finalmission.ballparkreservation.reservation.Reservation;
import finalmission.ballparkreservation.schedule.Schedule;
import org.springframework.test.util.ReflectionTestUtils;

public class TestFactory {

    public static Member memberWithId(Long id, Member member) {
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    public static Schedule scheduleWithId(Long id, Schedule schedule) {
        ReflectionTestUtils.setField(schedule, "id", id);
        return schedule;
    }

    public static Reservation reservationWithId(Long id, Reservation reservation) {
        ReflectionTestUtils.setField(reservation, "id", id);
        return reservation;
    }
}
