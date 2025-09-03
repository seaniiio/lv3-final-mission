package finalmission.ballparkreservation.reservation;

import finalmission.ballparkreservation.member.Member;
import finalmission.ballparkreservation.member.MemberRepository;
import finalmission.ballparkreservation.schedule.Schedule;
import finalmission.ballparkreservation.schedule.ScheduleRepository;
import finalmission.ballparkreservation.schedule.SeatRank;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

@DataJpaTest
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("어린이날 어린이 예약 정보들을 조회할 수 있다")
    void findByVisitDateAndIsChild() {
        // given
        LocalDate childDate = LocalDate.of(2025, 5, 5);

        Member child = memberRepository.save(new Member("child@gmail.com", "1234", "어린이", 13));
        Member adult = memberRepository.save(new Member("adult@gmail.com", "1234", "어른", 23));

        Schedule childrensDaySchedule1 = scheduleRepository.save(new Schedule(1, SeatRank.TABLE, childDate));
        Schedule childrensDaySchedule2 = scheduleRepository.save(new Schedule(2, SeatRank.TABLE, childDate));

        Reservation childrenReservation = reservationRepository.save(new Reservation(child, childrensDaySchedule1, true));
        Reservation adultReservation = reservationRepository.save(new Reservation(adult, childrensDaySchedule2, true));

        // when & then
        Assertions.assertThat(reservationRepository.findByVisitDateAndIsChild(childDate))
                .containsExactly(childrenReservation);
    }
}
