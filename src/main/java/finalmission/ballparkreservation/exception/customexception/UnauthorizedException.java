package finalmission.ballparkreservation.exception.customexception;

public class UnauthorizedException extends RuntimeException {

    private static final String MESSAGE = "접근 권한이 없습니다.";

    public UnauthorizedException() {
        super(MESSAGE);
    }
}
