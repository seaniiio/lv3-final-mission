package finalmission.ballparkreservation.member;

import finalmission.ballparkreservation.member.dto.MemberCreateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원 생성에 실패한 경우 예외를 발생시킨다")
    void create() {
        // given
        MemberCreateRequest request = new MemberCreateRequest("may@gmail.com", "1234", "may", 24);
        given(memberRepository.save(any()))
                .willThrow(new IllegalArgumentException());

        // when & then
        Assertions.assertThatThrownBy(() -> memberService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("id에 대한 회원이 존재하지 않는 경우 예외를 발생시킨다")
    void getById() {
        // given
        given(memberRepository.findById(any()))
                .willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> memberService.getById(any()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("이메일에 대한 회원이 존재하지 않는 경우 예외를 발생시킨다")
    void getByEmail() {
        // given
        given(memberRepository.findByEmail(any()))
                .willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> memberService.getByEmail(any()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("이메일에 대한 회원이 존재하는지 확인할 수 있다.")
    void existsByEmail() {
        // given
        given(memberRepository.existsByEmail(any()))
                .willReturn(true);

        // when & then
        Assertions.assertThat(memberService.existsByEmail(any())).isTrue();
    }
}
