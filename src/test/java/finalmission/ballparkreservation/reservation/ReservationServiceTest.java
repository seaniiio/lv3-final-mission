package finalmission.ballparkreservation.reservation;

import finalmission.ballparkreservation.auth.dto.LoginMember;
import finalmission.ballparkreservation.exception.customexception.UnauthorizedException;
import finalmission.ballparkreservation.external.HolidayClient;
import finalmission.ballparkreservation.member.Member;
import finalmission.ballparkreservation.member.MemberService;
import finalmission.ballparkreservation.reservation.dto.ReservationCreateRequest;
import finalmission.ballparkreservation.reservation.dto.ReservationCreateResponse;
import finalmission.ballparkreservation.reservation.dto.ReservationSeatUpdateRequest;
import finalmission.ballparkreservation.schedule.Schedule;
import finalmission.ballparkreservation.schedule.ScheduleService;
import finalmission.ballparkreservation.schedule.SeatRank;
import finalmission.ballparkreservation.util.TestFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private MemberService memberService;
    @Mock
    private ScheduleService scheduleService;
    @Mock
    private HolidayClient holidayClient;

    @Test
    @DisplayName("예약하려는 날짜, 좌석에 대한 예약이 이미 존재하는 경우 예외를 발생시킨다")
    void create1() {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest("TABLE", 10, LocalDate.now());
        LoginMember loginMember = new LoginMember(1L);
        Member member = TestFactory.memberWithId(1L, new Member("may@gmail.com", "1234", "메이", 24));
        given(memberService.getById(1L))
                .willReturn(member);
        given(scheduleService.getByRankAndNumberAndDate(SeatRank.TABLE, 10, request.date()))
                .willReturn(TestFactory.scheduleWithId(1L, new Schedule(10, SeatRank.TABLE, LocalDate.now())));
        given(reservationRepository.existsBySchedule(any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> reservationService.create(request, loginMember))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("앉을 좌석을 예약하면 금액을 반환한다.")
    void create2() {
        // given
        LocalDate date = LocalDate.of(2025, 6, 10);
        ReservationCreateRequest request = new ReservationCreateRequest("TABLE", 10, date);
        LoginMember loginMember = new LoginMember(1L);
        Member member = TestFactory.memberWithId(1L, new Member("may@gmail.com", "1234", "메이", 24));
        Schedule schedule = TestFactory.scheduleWithId(1L, new Schedule(10, SeatRank.TABLE, date));
        Reservation reservation = TestFactory.reservationWithId(1L, new Reservation(member, schedule, false));

        given(memberService.getById(1L))
                .willReturn(member);
        given(scheduleService.getByRankAndNumberAndDate(SeatRank.TABLE, 10, request.date()))
                .willReturn(schedule);
        given(reservationRepository.existsBySchedule(any()))
                .willReturn(false);
        given(holidayClient.getHolidaysOfYearAndMonth(date))
                .willReturn(List.of());
        given(reservationRepository.save(any()))
                .willReturn(reservation);

        // when
        ReservationCreateResponse response = reservationService.create(request, loginMember);

        // then
        Assertions.assertThat(response.amount())
                .isEqualTo(55000);
    }

    @Test
    @DisplayName("주말이 아니어도 공휴일인 경우 주말 가격이 적용된다")
    void create3() {
        // given
        LocalDate date = LocalDate.of(2025, 5, 5);
        ReservationCreateRequest request = new ReservationCreateRequest("TABLE", 10, date);
        LoginMember loginMember = new LoginMember(1L);
        Member member = TestFactory.memberWithId(1L, new Member("may@gmail.com", "1234", "메이", 24));
        Schedule schedule = TestFactory.scheduleWithId(1L, new Schedule(10, SeatRank.TABLE, date));
        Reservation reservation = TestFactory.reservationWithId(1L, new Reservation(member, schedule, true));

        given(memberService.getById(1L))
                .willReturn(member);
        given(scheduleService.getByRankAndNumberAndDate(SeatRank.TABLE, 10, request.date()))
                .willReturn(schedule);
        given(reservationRepository.existsBySchedule(any()))
                .willReturn(false);
        given(holidayClient.getHolidaysOfYearAndMonth(date))
                .willReturn(List.of(date));
        given(reservationRepository.save(any()))
                .willReturn(reservation);

        // when
        ReservationCreateResponse response = reservationService.create(request, loginMember);

        // then
        Assertions.assertThat(response.amount())
                .isEqualTo(88000);
    }

    @Test
    @DisplayName("예약을 생성한 사용자와 예약을 수정하려는 사용자가 일치하지 않는 경우 예외를 발생시킨다.")
    void update_memberNotAuthorizedError() {
        // given
        LoginMember loginMember = new LoginMember(2L);
        Member reservationMember = TestFactory.memberWithId(1L, new Member("may@gmail.com", "1234", "메이", 13));
        Schedule schedule = new Schedule(1, SeatRank.TABLE, LocalDate.of(2025, 5, 5));
        Long reservationId = 1L;
        Reservation reservation = TestFactory.reservationWithId(reservationId, new Reservation(reservationMember, schedule, true));
        Integer newSeatNumber = 2;
        ReservationSeatUpdateRequest request = new ReservationSeatUpdateRequest(reservationId, newSeatNumber);

        given(reservationRepository.findById(reservationId))
                .willReturn(Optional.of(reservation));

        // when & then
        assertThatThrownBy(() -> reservationService.update(request, loginMember))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("존재하지 않는 좌석으로 예약 변경을 시도하는 경우 예외를 발생시킨다.")
    void update_seatNotExistsError() {
        // given
        LoginMember loginMember = new LoginMember(1L);
        Member reservationMember = TestFactory.memberWithId(1L, new Member("may@gmail.com", "1234", "메이", 13));
        Schedule schedule = new Schedule(1, SeatRank.TABLE, LocalDate.of(2025, 5, 5));
        Long reservationId = 1L;
        Reservation reservation = TestFactory.reservationWithId(reservationId, new Reservation(reservationMember, schedule, true));
        Integer newSeatNumber = 500;
        ReservationSeatUpdateRequest request = new ReservationSeatUpdateRequest(reservationId, newSeatNumber);


        given(reservationRepository.findById(reservationId))
                .willReturn(Optional.of(reservation));
        given(reservationRepository.existsBySchedule_DateAndSchedule_RankAndSchedule_Number(schedule.getDate(), schedule.getRank(), newSeatNumber))
                .willReturn(false);
        given(scheduleService.getByRankAndNumberAndDate(schedule.getRank(), newSeatNumber, schedule.getDate()))
                .willThrow(IllegalArgumentException.class);

        // when & then
        assertThatThrownBy(() -> reservationService.update(request, loginMember))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("변경하려는 좌석 번호에 이미 예약이 존재하는 경우 예외를 발생시킨다.")
    void update_seatAlreadyReserved() {
        // given
        LoginMember loginMember = new LoginMember(1L);
        Member reservationMember = TestFactory.memberWithId(1L, new Member("may@gmail.com", "1234", "메이", 13));
        Schedule schedule = new Schedule(1, SeatRank.TABLE, LocalDate.of(2025, 5, 5));
        Long reservationId = 1L;
        Reservation reservation = TestFactory.reservationWithId(reservationId, new Reservation(reservationMember, schedule, true));
        Integer newSeatNumber = 2;
        ReservationSeatUpdateRequest request = new ReservationSeatUpdateRequest(reservationId, newSeatNumber);

        given(reservationRepository.findById(reservationId))
                .willReturn(Optional.of(reservation));
        given(reservationRepository.existsBySchedule_DateAndSchedule_RankAndSchedule_Number(schedule.getDate(), schedule.getRank(), newSeatNumber))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> reservationService.update(request, loginMember))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약을 생성한 사용자와 예약을 삭제하려는 사용자가 일치하지 않는 경우 예외를 발생시킨다.")
    void delete_memberNotAuthorizedError() {
        // given
        LoginMember loginMember = new LoginMember(2L);
        Member reservationMember = TestFactory.memberWithId(1L, new Member("may@gmail.com", "1234", "메이", 13));
        Schedule schedule = new Schedule(1, SeatRank.TABLE, LocalDate.of(2025, 5, 5));
        Long reservationId = 1L;
        Reservation reservation = TestFactory.reservationWithId(reservationId, new Reservation(reservationMember, schedule, true));

        given(reservationRepository.findById(reservationId))
                .willReturn(Optional.of(reservation));

        // when & then
        assertThatThrownBy(() -> reservationService.delete(reservationId, loginMember))
                .isInstanceOf(UnauthorizedException.class);
    }
}
