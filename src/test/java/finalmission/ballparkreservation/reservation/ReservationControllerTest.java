package finalmission.ballparkreservation.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import finalmission.ballparkreservation.auth.JwtProvider;
import finalmission.ballparkreservation.auth.dto.LoginMember;
import finalmission.ballparkreservation.reservation.dto.ReservationCreateRequest;
import finalmission.ballparkreservation.reservation.dto.ReservationCreateResponse;
import finalmission.ballparkreservation.schedule.SeatRank;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private JwtProvider jwtProvider;

    @Nested
    class Create {

        @Test
        @DisplayName("/reservations POST 예약 생성 요청에 성공하면 201 CREATED 코드와 금액을 응답한다")
        void create_success() throws Exception {
            // given
            final String token = "iamtoken";
            final LoginMember loginMember = new LoginMember(1L);
            final ReservationCreateRequest request = new ReservationCreateRequest(SeatRank.TABLE.name(), 1, LocalDate.of(2025, 5, 5));
            final int amount = 88000;

            given(jwtProvider.isValidToken(token))
                    .willReturn(true);
            given(jwtProvider.getSubjectByToken(token))
                    .willReturn(loginMember.id().toString());
            given(reservationService.create(request, loginMember))
                    .willReturn(new ReservationCreateResponse(amount));

            // when & then
            mockMvc.perform(post("/reservations")
                            .cookie(new Cookie("token", token))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.amount").value(amount));
        }

        @Test
        @DisplayName("/reservations POST 예약 생성 요청에서 인가에 실패하는 경우 401 UNAUTHORIZED 를 응답한다.")
        void create_unauthorizedFail() throws Exception {
            // given
            final String token = "iamtoken";
            final ReservationCreateRequest request = new ReservationCreateRequest(SeatRank.TABLE.name(), 1, LocalDate.of(2025, 5, 5));

            given(jwtProvider.isValidToken(token))
                    .willReturn(false);

            // when & then
            mockMvc.perform(post("/reservations")
                            .cookie(new Cookie("token", token))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }
}
