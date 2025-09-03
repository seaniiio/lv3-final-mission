package finalmission.ballparkreservation.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public Schedule getByRankAndNumberAndDate(final SeatRank rank, final int number, final LocalDate date) {
        return scheduleRepository.findByRankAndNumberAndDate(rank, number, date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜에 대한 좌석을 찾을 수 없습니다."));
    }
}
