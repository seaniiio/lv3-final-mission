package finalmission.ballparkreservation.auth;

import finalmission.ballparkreservation.auth.dto.LoginRequest;
import finalmission.ballparkreservation.auth.dto.SignupRequest;
import finalmission.ballparkreservation.member.Member;
import finalmission.ballparkreservation.member.MemberService;
import finalmission.ballparkreservation.member.dto.MemberCreateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    public String login(LoginRequest request) { // TODO : dto 서비스용으로 고치기
        Member member = memberService.getByEmail(request.email());
        if (member.isPasswordMatch(request.password())) {
            return jwtProvider.createToken(member);
        }
        throw new IllegalArgumentException("로그인에 실패했습니다.");
    }

    @Transactional
    public void signup(final SignupRequest request) { // TODO : dto 서비스용으로 고치기
        if (memberService.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest(request.email(), request.password(), request.name(), request.age());
        memberService.create(memberCreateRequest);
    }
}
