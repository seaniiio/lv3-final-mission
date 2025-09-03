package finalmission.ballparkreservation.auth;

import finalmission.ballparkreservation.auth.dto.LoginRequest;
import finalmission.ballparkreservation.auth.dto.SignupRequest;
import finalmission.ballparkreservation.member.Member;
import finalmission.ballparkreservation.member.MemberService;
import finalmission.ballparkreservation.util.TestFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;
    @Mock
    private MemberService memberService;
    @Mock
    private JwtProvider jwtProvider;

    @DisplayName("이미 동일한 이메일이 존재하는 경우 회원가입 할 수 없다.")
    @Test
    void signup1() {
        // given
        String email = "may@gmail.com";
        SignupRequest request = new SignupRequest(email, "1234", "메이", 14);

        // when
        given(memberService.existsByEmail(email))
                .willReturn(true);

        // then
        Assertions.assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("로그인에 성공한 경우 토큰을 반환한다.")
    @Test
    void login1() {
        // given
        String email = "may@gmail.com";
        LoginRequest request = new LoginRequest(email, "1234");
        Member member = TestFactory.memberWithId(1L, new Member(email, "1234", "메이", 14));

        // when
        String token = "token!!";
        given(jwtProvider.createToken(member))
                .willReturn(token);
        given(memberService.getByEmail(email))
                .willReturn(member);

        // then
        Assertions.assertThat(authService.login(request)).isEqualTo(token);
    }

    @DisplayName("비밀번호가 일치하지 않는 경우 로그인할 수 없다.")
    @Test
    void login2() {
        // given
        String email = "may@gmail.com";
        LoginRequest request = new LoginRequest(email, "1234");
        Member member = TestFactory.memberWithId(1L, new Member("notValidEmail", "abce", "메이", 14));

        // when
        given(memberService.getByEmail(email))
                .willReturn(member);

        // then
        Assertions.assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
