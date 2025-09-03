package finalmission.ballparkreservation.schedule.dto;

import finalmission.ballparkreservation.schedule.Schedule;

import java.time.LocalDate;

public record ScheduleResponse(
        int number,
        String rank,
        LocalDate date
) {
    public static ScheduleResponse from(final Schedule schedule) {
        return new ScheduleResponse(schedule.getNumber(), schedule.getRank().name(), schedule.getDate());
    }
}
