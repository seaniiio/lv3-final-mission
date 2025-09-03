package finalmission.ballparkreservation.reservation;

import finalmission.ballparkreservation.auth.dto.LoginMember;
import finalmission.ballparkreservation.exception.customexception.UnauthorizedException;
import finalmission.ballparkreservation.external.HolidayClient;
import finalmission.ballparkreservation.member.Member;
import finalmission.ballparkreservation.member.MemberService;
import finalmission.ballparkreservation.reservation.dto.*;
import finalmission.ballparkreservation.schedule.Schedule;
import finalmission.ballparkreservation.schedule.ScheduleService;
import finalmission.ballparkreservation.schedule.SeatRank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberService memberService;
    private final ScheduleService scheduleService;
    private final HolidayClient holidayClient;

    @Transactional
    public ReservationCreateResponse create(final ReservationCreateRequest request, final LoginMember loginMember) {
        Member member = memberService.getById(loginMember.id());
        int seatNumber = request.seatNumber();
        SeatRank seatRank = SeatRank.fromName(request.seatRank());

        Schedule schedule = scheduleService.getByRankAndNumberAndDate(seatRank, seatNumber, request.date());
        validateReservationIsAbleToCreate(schedule);

        boolean isHoliday = checkHoliday(request.date());
        Reservation reservation = new Reservation(member, schedule, isHoliday);

        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationCreateResponse.from(savedReservation);
    }

    private boolean checkHoliday(final LocalDate date) {
        List<LocalDate> holidaysOfYearAndMonth = holidayClient.getHolidaysOfYearAndMonth(date);
        return holidaysOfYearAndMonth.contains(date);
    }

    private void validateReservationIsAbleToCreate(final Schedule schedule) {
        if (reservationRepository.existsBySchedule(schedule)) {
            throw new IllegalArgumentException("이미 해당 날짜 좌석에 대한 예약이 존재합니다.");
        }
    }

    public List<ReservationResponse> getAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberReservationResponse> getAllByMember(final LoginMember loginMember) {
        return reservationRepository.findAllByMember_Id(loginMember.id()).stream()
                .map(MemberReservationResponse::from)
                .toList();
    }

    @Transactional
    public void update(final ReservationSeatUpdateRequest request, final LoginMember requestMember) {
        final Reservation reservation = getById(request.id());
        validateAuthorization(reservation.getMember().getId(), requestMember.id());

        final Schedule schedule = reservation.getSchedule();
        validateIfScheduleAvailable(schedule.getDate(), schedule.getRank(), request.seatNumber());

        Schedule newSchedule = scheduleService.getByRankAndNumberAndDate(schedule.getRank(), request.seatNumber(), schedule.getDate());
        reservation.updateSchedule(newSchedule);
    }

    private Reservation getById(final Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id에 대한 예약이 존재하지 않습니다."));
    }

    @Transactional
    public void delete(final Long id, final LoginMember requestMember) {
        final Reservation reservation = getById(id);
        validateAuthorization(reservation.getMember().getId(), requestMember.id());
        reservationRepository.deleteById(id);
    }

    private void validateAuthorization(final Long reservationMemberId, final Long requestMemberId) {
        if (!reservationMemberId.equals(requestMemberId)) {
            throw new UnauthorizedException();
        }
    }

    private void validateIfScheduleAvailable(final LocalDate date, final SeatRank rank, final int seatNumber) {
        boolean isReservationAlreadyExist = reservationRepository.existsBySchedule_DateAndSchedule_RankAndSchedule_Number(date, rank, seatNumber);
        if (isReservationAlreadyExist) {
            throw new IllegalArgumentException("이미 해당 좌석에 대한 예약이 존재합니다.");
        }
    }
}
