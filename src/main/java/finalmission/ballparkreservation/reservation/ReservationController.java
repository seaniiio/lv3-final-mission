package finalmission.ballparkreservation.reservation;

import finalmission.ballparkreservation.auth.MemberAuthentication;
import finalmission.ballparkreservation.auth.dto.LoginMember;
import finalmission.ballparkreservation.reservation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationCreateResponse> create(
            @Valid @RequestBody ReservationCreateRequest request,
            @MemberAuthentication LoginMember member
    ) {
        ReservationCreateResponse response = reservationService.create(request, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAll(
            @MemberAuthentication LoginMember member
    ) {
        List<ReservationResponse> reservations = reservationService.getAll();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/my")
    public ResponseEntity<List<MemberReservationResponse>> getAllByMember(
            @MemberAuthentication LoginMember member
    ) {
        List<MemberReservationResponse> reservations = reservationService.getAllByMember(member);
        return ResponseEntity.ok(reservations);
    }

    @PatchMapping("/seat")
    public ResponseEntity<Void> updateSeat(
            @Valid @RequestBody ReservationSeatUpdateRequest request,
            @MemberAuthentication LoginMember member
    ) {
        reservationService.update(request, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @MemberAuthentication LoginMember member
    ) {
        reservationService.delete(id, member);
        return ResponseEntity.ok().build();
    }
}
