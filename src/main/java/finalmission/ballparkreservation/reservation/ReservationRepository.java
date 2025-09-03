package finalmission.ballparkreservation.reservation;

import finalmission.ballparkreservation.schedule.Schedule;
import finalmission.ballparkreservation.schedule.SeatRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsBySchedule(Schedule schedule);

    List<Reservation> findAllByMember_Id(Long id);

    boolean existsBySchedule_DateAndSchedule_RankAndSchedule_Number(LocalDate date, SeatRank rank, int seatNumber);

    @Query(value = """
            SELECT r.*
            FROM reservation r
            JOIN schedule s ON r.schedule_id = s.id
            JOIN member m ON r.member_id = m.id
            WHERE s.date = CAST(:date AS DATE) AND m.age < 15;
            """, nativeQuery = true)
    List<Reservation> findByVisitDateAndIsChild(final LocalDate date);
}
