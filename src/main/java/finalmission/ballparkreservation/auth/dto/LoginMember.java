package finalmission.ballparkreservation.auth.dto;

public record LoginMember(
        Long id
) {

    public static LoginMember createLoginMemberBySubject(String memberId) {
        return new LoginMember(Long.valueOf(memberId));
    }
}
