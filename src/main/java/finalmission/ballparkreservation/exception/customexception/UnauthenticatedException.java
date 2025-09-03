package finalmission.ballparkreservation.exception.customexception;

public class UnauthenticatedException extends RuntimeException {

    private static final String MESSAGE = "로그인 요청 정보가 올바르지 않습니다.";

    public UnauthenticatedException() {
        super(MESSAGE);
    }
}
