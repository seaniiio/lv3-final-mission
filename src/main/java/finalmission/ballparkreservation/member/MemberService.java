package finalmission.ballparkreservation.member;

import finalmission.ballparkreservation.member.dto.MemberCreateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void create(final MemberCreateRequest request) {
        Member member = new Member(request.email(), request.password(), request.name(), request.age());
        try {
            memberRepository.save(member);
        } catch (Exception e) {
            throw new IllegalArgumentException("회원 생성에 실패했습니다.");
        }
    }

    @Transactional(readOnly = true)
    public Member getById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Transactional(readOnly = true)
    public Member getByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 회원입니다."));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(final String email) {
        return memberRepository.existsByEmail(email);
    }
}
